package wx.toolkits.storage.pdf.converter.docx;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import lombok.SneakyThrows;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 16/6/18.
 */

/**
 * @function 从DOCX到ＰＤＦ的转化类
 */
public class POIConverter {


    /**
     * @param inpuFile 输入的文件流
     * @param outFile  输出的文件对象
     * @return
     * @function 利用Apache POI从输入的文件中生成PDF文件
     */
    @SneakyThrows
    public static void convertWithPOI(InputStream inpuFile, File outFile) {

        //从输入的文件流创建对象
        XWPFDocument document = new XWPFDocument(inpuFile);

        //创建PDF选项
        PdfOptions pdfOptions = PdfOptions.create();//.fontEncoding("windows-1250")

        //为输出文件创建目录
        outFile.getParentFile().mkdirs();

        //执行PDF转化
        PdfConverter.getInstance().convert(document, new FileOutputStream(outFile), pdfOptions);

    }

    @Test
    public void test_convertWithPOI() {

        //获取测试的输入流
        InputStream inpuFile = POIConverter.class.getResourceAsStream("/storage/big.docx");

        //输出流放置到build/pdf目录下
        File outFile = new File("build/pdf/convertWithPOI_big.pdf");

        long startTime = System.currentTimeMillis();

        //调用PDF转化类
        POIConverter.convertWithPOI(inpuFile, outFile);

        System.out.println("利用Apache POI进行转化,耗时:" + (System.currentTimeMillis() - startTime));
    }

    /**
     * @param inpuFile
     * @param outFile
     * @param renderParams
     * @function 先将渲染参数填入模板DOCX文件然后生成PDF
     */
    @SneakyThrows
    public static void convertFromTemplateWithFreemarker(InputStream inpuFile, File outFile, Map<String, Object> renderParams) {

        //创建Report实例
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(
                inpuFile, TemplateEngineKind.Freemarker);

        //创建上下文
        IContext context = report.createContext();

        //填入渲染参数
        renderParams.forEach((s, o) -> {
            context.put(s, o);
        });

        //创建输出流
        outFile.getParentFile().mkdirs();

        //创建转化参数
        Options options = Options.getTo(ConverterTypeTo.PDF).via(
                ConverterTypeVia.XWPF);

        //执行转化过程
        report.convert(context, options, new FileOutputStream(outFile));
    }

    @Test
    public void test_convertFromTemplateWithFreemarker() {

        //获取测试的输入流
        InputStream inpuFile = POIConverter.class.getResourceAsStream("/storage/big.docx");

        //输出流放置到build/pdf目录下
        File outFile = new File("build/pdf/convertFromTemplateWithFreemarker_big.pdf");

        //创建渲染参数
        Map<String, Object> renderParams = new HashMap<>();

        renderParams.put("title", "Struts2  IDE - WTP XML Search Engine");

        long startTime = System.currentTimeMillis();

        //调用PDF转化类
        POIConverter.convertFromTemplateWithFreemarker(inpuFile, outFile, renderParams);

        System.out.println("利用Apache POI进行转化,耗时:" + (System.currentTimeMillis() - startTime));

    }

}
