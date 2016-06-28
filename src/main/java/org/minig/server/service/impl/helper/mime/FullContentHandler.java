//package org.minig.server.service.impl.helper.mime;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//import org.apache.james.mime4j.MimeException;
//import org.apache.james.mime4j.dom.Header;
//import org.apache.james.mime4j.dom.field.ContentDispositionField;
//import org.apache.james.mime4j.stream.BodyDescriptor;
//import org.apache.james.mime4j.stream.Field;
//
//public class FullContentHandler extends TestContentHandler {
//
//    @Override
//    public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
//        System.out.println("body: " + bd);
//
//        // ByteArrayInputStream in = new ByteArrayInputStream();
//
//        // System.out.println(bd.getMimeType());
//    }
//
//    @Override
//    public void headers(Header header) {
//        super.headers(header);
//
//        for (Field f : header) {
//            switch(f.getName()) {
//
//            case "Content-Disposition":
//                ContentDispositionField cd = (ContentDispositionField) f;
//
//                // System.out.println(cd.getDispositionType());
//                break;
//
//            default:
//                // System.out.println(f);
//            }
//            System.out.println(f);
//        }
//
//        // System.out.println("header: " + header + " : " + header.get);
//
//        /*
//         * body: [mimeType=text/html, mediaType=text, subType=html,
//         * boundary=null, charset=utf-8] header: Content-Type: image/png;
//         * name=logo.png Content-Transfer-Encoding: base64 Content-Disposition:
//         * inline; filename=logo.png Content-ID:
//         * <1367760625.51865ef16cc8c@swift.generated>
//         */
//    }
// }
