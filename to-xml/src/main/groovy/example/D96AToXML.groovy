package example

import org.milyn.Smooks
import org.milyn.SmooksException
import org.milyn.smooks.edi.unedifact.UNEdifactReaderConfigurator
import org.xml.sax.SAXException

import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import java.nio.channels.FileChannel

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 10/25/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
class D96AToXML {

    protected static void runSmooksTransform(String inputFile, String outputFile) throws IOException, SAXException, SmooksException {


        Smooks smooks = new Smooks();
        smooks.setReaderConfig(new UNEdifactReaderConfigurator("urn:org.milyn.edi.unedifact:d96a-mapping:*"));

        try {
            StringWriter writer = new StringWriter();

            smooks.filterSource(new StreamSource(new FileInputStream(inputFile)), new StreamResult(writer));

            String result = writer.toString();

            PrintWriter out = new PrintWriter(outputFile);

            out.print(result);
            out.close();

        } finally {
            smooks.close();
        }
    }

    def static edi_dir = '/home/alex/Documents/dr-oetker-docs/facturi-edi/'
    def static edi_xml = '/home/alex/Documents/dr-oetker-docs/facturi-xml/'

    def static convert_to_xml = {
        def new_name = it - 'ido' + 'xml'
        D96AToXML.runSmooksTransform(edi_dir + it, edi_xml + new_name)
    }

    def static check_multiple_interchanges = {
        def edi_file = edi_dir + it

        def number = (new File(edi_file).getText() =~ /.+?UNZ\+([0-9]+)\+.*/)[0][1]
        number == '1'
    }

    public static void main(args) {

        new File(edi_dir).list()
                .grep( { it.endsWith('ido') } )
                .grep( check_multiple_interchanges )
                .collect(convert_to_xml)
    }

}
