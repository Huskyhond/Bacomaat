package MLB;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;

public class LabelWriter {

    public static final String PRINTERNAME = "DYMO LabelWriter";
    /**
     * true if you want a menu to select the printer
     */
    public static final boolean PRINTMENU = false;
    public static String printThis[] = new String[11];
    PrinterJob printerJob = PrinterJob.getPrinterJob();
    PageFormat pageFormat = printerJob.defaultPage();
    Paper paper = new Paper();

    public void printLabel(final String[] text) {


        final double widthPaper = (7 * 72);
        final double heightPaper = (10.5 * 72);

        paper.setSize(widthPaper, heightPaper);
        paper.setImageableArea(0, 0, widthPaper, heightPaper);

        pageFormat.setPaper(paper);

        pageFormat.setOrientation(PageFormat.PORTRAIT);


        PrintService[] printService = PrinterJob.lookupPrintServices();

        for (int i = 0; i < printService.length; i++) {
            System.out.println(printService[i].getName());

            if (printService[i].getName().contains(PRINTERNAME)) {
                try {
                    printerJob.setPrintService(printService[i]);
                    printerJob.setPrintable(new Printable() {
                        @Override
                        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                                throws PrinterException {
                            if (pageIndex < getPageNumbers()) 
                            {
                                Graphics2D g = (Graphics2D) graphics;
                                g.translate(20, 10);

                                String value = "";

                                int x = 10;
                                int y = 30;
                                
                                InputStream ttf = this.getClass().getResourceAsStream("3of9.TTF");

                                Font font = null;
                                try {
                                    font = Font.createFont(Font.TRUETYPE_FONT, ttf);
                                } catch (FontFormatException | IOException ex) {
                                    Logger.getLogger(LabelWriter.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                g.setFont(font);
                                
                                //Welcome to
                                g.setFont(new Font(g.getFont().getFontName(), g.getFont().getStyle(), 8));
                                value = text[0];
                                g.drawString(value, x, y);               
                                
                                //MLB
                                g.setFont(new Font(g.getFont().getFontName(), g.getFont().getStyle(), 24));
                                value = text[1];
                                y+=20;
                                g.drawString(value, x, y);

                                //De rest printen...
                                y+=10;
                                for(int i=2;i<text.length;i++)
                                {
                                	g.setFont(new Font(g.getFont().getFontName(), g.getFont().getStyle(), 8));
                                    value = text[i];
                                    y+=10;
                                    g.drawString(value, x, y);
                                }

                                return PAGE_EXISTS;
                            } else {
                                return NO_SUCH_PAGE;
                            }
                        }
                    }, pageFormat); // The 2nd param is necessary for printing into a label width a right landscape format.
                    printerJob.print();
                } catch (PrinterException e) {
                    e.printStackTrace();
                }
            }
        }


//        System.exit(0);

    }

    public int getPageNumbers() {
        return 1;
    }
}

