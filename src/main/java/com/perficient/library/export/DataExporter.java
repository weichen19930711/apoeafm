package com.perficient.library.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;

public abstract class DataExporter extends ExportXLS {

    private String fileName;

    private HttpServletResponse response;

    public DataExporter(){
        
    }
    public DataExporter(String fileName, HttpServletResponse response) {
        this.fileName = fileName;
        this.response = response;
    }

    public abstract void doExport() throws IOException;

    public abstract InputStream getInput();

    public abstract String getContentType();

    public void export(Context context) throws IOException {
        doExport();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType(getContentType());
        InputStream in = getInput();
        OutputStream out = response.getOutputStream();
        generateTemplateFile(in, out, context);

        if(in != null && out != null){
            in.close();
            out.close();            
        }
    }
}
