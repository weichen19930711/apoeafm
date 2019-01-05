package com.perficient.library.export;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;

import com.perficient.library.common.converter.BookStatusAttributeConverter;
import com.perficient.library.common.converter.PurchaserAttributeConverter;
import com.perficient.library.common.utils.DateFormatUtil;

public abstract class ExportXLS {

    private static final String funcKeyDateFormatUtil = "dateFormatUtil";

    private static final String funcKeyBookStatusAttributeConverter = "bookStatusAttributeConverter";
    
    private static final String funcKeyPurchaserAttributeConverter = "purchaserAttributeConverter";
    
    protected void generateTemplateFile(InputStream in, OutputStream out, Context context) {
        try {
            JxlsHelper jxlsHelper = JxlsHelper.getInstance();
            Transformer transformer = jxlsHelper.createTransformer(in, out);
            JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator) transformer.getTransformationConfig()
                .getExpressionEvaluator();
            Map<String, Object> funcs = new HashMap<String, Object>();
            funcs.put(funcKeyDateFormatUtil, new DateFormatUtil());
            funcs.put(funcKeyBookStatusAttributeConverter, new BookStatusAttributeConverter());
            funcs.put(funcKeyPurchaserAttributeConverter, new PurchaserAttributeConverter());
            evaluator.getJexlEngine().setFunctions(funcs);
            jxlsHelper.processTemplate(context, transformer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void export(HttpServletResponse response, String exportFileName, String importFilePath, Context context)
        throws IOException {
        new ExcelExporter(response, exportFileName, importFilePath) {

            private InputStream inputStream = null;

            @Override
            public void doExport() throws IOException {
                inputStream = new BufferedInputStream(getClass().getResourceAsStream(importFilePath));
                this.setInputStream(inputStream);
            }
        }.export(context);
    }
}
