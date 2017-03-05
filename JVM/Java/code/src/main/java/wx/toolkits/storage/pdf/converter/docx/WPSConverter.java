package wx.toolkits.storage.pdf.converter.docx;


import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by lotuc on 8/31/2016.
 */
public class WPSConverter {
    public static final Logger logger = Logger.getLogger("org.lotuc.Doc2Pdf");

    public static interface Converter {
        boolean convert(String word, String pdf);
    }

    public static class Wps implements Converter {
        @Override
        public boolean convert(String word, String pdf) {
            File pdfFile = new File(pdf);
            File wordFile = new File(word);
            boolean convertSuccessfully = false;

            ActiveXComponent wps = null;
            ActiveXComponent doc = null;


            try {
                wps = new ActiveXComponent("KWPS.Application");

//                Dispatch docs = wps.getProperty("Documents").toDispatch();
//                Dispatch d = Dispatch.call(docs, "Open", wordFile.getAbsolutePath(), false, true).toDispatch();
//                Dispatch.call(d, "SaveAs", pdfFile.getAbsolutePath(), 17);
//                Dispatch.call(d, "Close", false);

                doc = wps.invokeGetComponent("Documents")
                        .invokeGetComponent("Open", new Variant(wordFile.getAbsolutePath()));

                try {
                    doc.invoke("SaveAs",
                            new Variant(new File("C:\\Users\\lotuc\\Documents\\mmm.pdf").getAbsolutePath()),
                            new Variant(17));
                    convertSuccessfully = true;
                } catch (Exception e) {
                    logger.warning("生成PDF失败");
                    e.printStackTrace();
                }

                File saveAsFile = new File("C:\\Users\\lotuc\\Documents\\saveasfile.doc");
                try {
                    doc.invoke("SaveAs", saveAsFile.getAbsolutePath());
                    logger.info("成功另存为" + saveAsFile.getAbsolutePath());
                } catch (Exception e) {
                    logger.info("另存为" + saveAsFile.getAbsolutePath() + "失败");
                    e.printStackTrace();
                }
            } finally {
                if (doc == null) {
                    logger.info("打开文件 " + wordFile.getAbsolutePath() + " 失败");
                } else {
                    try {
                        logger.info("释放文件 " + wordFile.getAbsolutePath());
                        doc.invoke("Close");
                        doc.safeRelease();
                    } catch (Exception e1) {
                        logger.info("释放文件 " + wordFile.getAbsolutePath() + " 失败");
                    }
                }

                if (wps == null) {
                    logger.info("加载 WPS 控件失败");
                } else {
                    try {
                        logger.info("释放 WPS 控件");
                        wps.invoke("Quit");
                        wps.safeRelease();
                    } catch (Exception e1) {
                        logger.info("释放 WPS 控件失败");
                    }
                }
            }

            return convertSuccessfully;
        }
    }

    public static void main(String[] args) {
        System.out.println("hello world");

        Converter converter = new Wps();
        converter.convert("C:\\Users\\lotuc\\Documents\\test.doc", "C:\\Users\\lotuc\\Documents\\test.pdf");
    }
}
