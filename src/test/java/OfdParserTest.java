import org.junit.Test;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

import pers.gongdaowen.ofd.OFDInfo;
import pers.gongdaowen.ofd.OfdParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class OfdParserTest {

    @Test
    public void parseOfd() throws Exception {
        OFDInfo ofd = OfdParser.parse(new File("src/test/resources/百望云增值税发票.ofd"));
//        System.out.println("验签：" + ofd.verify());
//        Invoice invoice = ofd.getInvoice();
//        System.out.println("发票信息：" + invoice);
        BufferedImage image = ofd.convertAsSingleImage(150);
        ImageIO.write(image, "JPG", new File("src/test/resources/百望云增值税发票.ofd.jpg"));
        
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("src/test/resources/百望云增值税发票.ofd.jpg.pdf"));
        document.open();
        //设置PDF页大小
        document.setPageSize(new Rectangle(image.getWidth(), image.getHeight()));
        document.setMargins(0, 20f, 0, 0);
        document.newPage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "JPG", baos);
        Image iTextImage = Image.getInstance(baos.toByteArray());
        document.add(iTextImage);
        document.close();
    }
}
