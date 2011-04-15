/**
 * Copyright 2010 OpenEngSB Division, Vienna University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openengsb.connector.promreport.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.openengsb.connector.promreport.internal.model.ProcessInstancePointer;
import org.openengsb.connector.promreport.internal.mxml.Data;
import org.openengsb.connector.promreport.internal.mxml.Process;
import org.openengsb.connector.promreport.internal.mxml.ProcessInstance;
import org.openengsb.connector.promreport.internal.mxml.WorkflowLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps for each process one file in mxml format.
 */
public class ProcessFileStore implements MxmlStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessFileStore.class);
    
    private File processDir;
    
    private final JAXBContext jaxbContext;
    private final XMLInputFactory xmlif;
    private Schema schema;
    
    private static final String ENDDOC = "    </Process>\n</WorkflowLog>\n";
    
    public ProcessFileStore(File rootDirectory) {
        this.processDir = rootDirectory;
        if (!rootDirectory.exists() && !rootDirectory.mkdirs()) {
            throw new RuntimeException("Could not make directory " + rootDirectory.getAbsolutePath());
        } else if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("Root directory '" + rootDirectory + "' is not a directory.");
        }
        try {
            jaxbContext = JAXBContext.newInstance(WorkflowLog.class);
            xmlif = XMLInputFactory.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                new URL("http://is.tm.tue.nl/research/processmining/WorkflowLog.xsd"));
        } catch (Exception e) {
            LOGGER.warn("Error during creating of Mxml schema. Continue without schema validation.", e);
        }
    }

    /**
     * Keeps for each Process one global file.
     * Appends each process from the given workflow to the corresponding file.
     */
    @Override
    public void persist(WorkflowLog workflow) {
        if (workflow == null) {
            return;
        }
        for (Process pro : workflow.getProcess()) {
            if (pro.getId() != null) {
                for (ProcessInstance pi : pro.getProcessInstance()) {
                    append(pro.getId(), pi);
                }
            }
        }
    }

    private void append(String proId, ProcessInstance pi) {
        File proFile = getFile(proId);
        writeTo(proFile, pi);
    }
    
    private void writeTo(File proFile, ProcessInstance pi) {
        LOGGER.debug("write process instance {} to {}", pi.getId(), proFile.getName());
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(proFile, "rw");
            long position = findLastProcess(raf);
            raf.seek(position);
            String s = marshal(pi);
            raf.writeBytes(s);
            raf.write(0x0A);
            raf.writeBytes(ENDDOC);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
            }
        }
    }

    private File getFile(String processId) {
        File mxmlFile = new File(processDir, getFileName(processId));
        createFile(mxmlFile, processId);
        return mxmlFile;
    }
    
    private void createFile(File mxmlFile, String processId) {
        LOGGER.debug(String.format("create file %s for process %s", mxmlFile.getName(), processId));
        try {
            if (mxmlFile.createNewFile()) {
                Marshaller m = jaxbContext.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, 
                    "http://is.tm.tue.nl/research/processmining/WorkflowLog.xsd");
                m.setSchema(schema);
                m.marshal(createWorkflowLogTemplate(processId), mxmlFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private WorkflowLog createWorkflowLogTemplate(String processId) {
        WorkflowLog workflow = new WorkflowLog();
        Process p = new Process();
        p.setId(processId);
        Data data = new Data();
        Data.Attribute att = new Data.Attribute();
        att.setName("CreationTime");
        att.setValue(new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
        data.getAttribute().add(att);
        p.setData(data);
        workflow.getProcess().add(p);
        return workflow;
    }

    private String getFileName(String proId) {
        return proId.hashCode() + ".mxml";
    }
    
    private long findLastProcess(RandomAccessFile raf) throws IOException {
        final String proc = "</Process>";
        final byte[] bproc = proc.getBytes();
        final int len = proc.length();
        for (long i = raf.length() - "</Process></WorkflowLog>".length(); i >= 0; i--) {
            byte[] buf = new byte[len];
            raf.seek(i);
            raf.readFully(buf, 0, len);
            int b;
            for (b = 0; b < len; b++) {
                if (buf[b] != bproc[b]) {
                    break;
                }
            }
            if (b == len) {
                return i;
            }
        }
        return -1;
    }

    private String marshal(ProcessInstance pi) throws JAXBException, IOException {
        Marshaller m = createMarshaller();
        StringWriter sw = new StringWriter();
        m.marshal(pi, sw);
        sw.close();
        return "    " + sw.toString().replace("\n", "\n        ");
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        m.setSchema(schema);
        return m;
    }

    @Override
    public ProcessInstance read(ProcessInstancePointer pipoint) {
        return read(pipoint.getProcessId(), pipoint.getProcessInstanceId());
    }

    @Override
    public ProcessInstance read(Long processId, String processInstanceId) {
        if (processId == null) {
            return null;
        }
        if (processInstanceId == null) {
            return null;
        }
        File processFile = new File(processDir, getFileName(String.valueOf(processId)));
        if (!processFile.exists()) {
            return null;
        }
        return readFrom(processFile, processInstanceId);
    }

    private ProcessInstance readFrom(File processFile, String processInstanceId) {
        BufferedInputStream fis = null;
        try {
            fis = new BufferedInputStream(new FileInputStream(processFile));
            XMLStreamReader xmlr = xmlif.createXMLStreamReader(fis);
            int eventType;
            while (xmlr.hasNext()) {
                eventType = xmlr.next();
                if (eventType == XMLStreamConstants.START_ELEMENT && xmlr.getLocalName().equals("ProcessInstance") 
                        && xmlr.getAttributeValue(null, "id").equals(processInstanceId)) {
                    return (ProcessInstance) createUnmarshaller().unmarshal(xmlr);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return null;
    }

    private Unmarshaller createUnmarshaller() throws JAXBException {
        Unmarshaller u = jaxbContext.createUnmarshaller();
        u.setSchema(schema);
        return u;
    }

}
