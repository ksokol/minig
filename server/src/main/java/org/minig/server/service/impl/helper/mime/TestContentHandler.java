//package org.minig.server.service.impl.helper.mime;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.apache.james.mime4j.MimeException;
//import org.apache.james.mime4j.dom.Header;
//import org.apache.james.mime4j.dom.address.Address;
//import org.apache.james.mime4j.dom.field.AddressListField;
//import org.apache.james.mime4j.dom.field.DateTimeField;
//import org.apache.james.mime4j.dom.field.MailboxListField;
//import org.apache.james.mime4j.message.SimpleContentHandler;
//import org.apache.james.mime4j.stream.BodyDescriptor;
//import org.apache.james.mime4j.stream.Field;
//import org.minig.server.MailMessage;
//import org.minig.server.MailMessageAddress;
//
//public class TestContentHandler extends SimpleContentHandler {
//
//    protected MailMessage mm = new MailMessage();
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
//        // TODO Auto-generated method stub
//        // System.out.println(header);
//
//        // System.out.println(header.getField("Date").get);
//
//        for (Field f : header) {
//
//            switch(f.getName()) {
//            case "Message-ID":
//                mm.setMessageId(f.getBody());
//                break;
//            case "From":
//                MailboxListField dd = ((MailboxListField) f);
//
//                MailMessageAddress mailMessageAddress = new MailMessageAddress();
//
//                mailMessageAddress.setEmail(dd.getMailboxList().get(0).getAddress());
//                mailMessageAddress.setDisplayName(dd.getMailboxList().get(0).getName());
//
//                mm.setSender(mailMessageAddress);
//
//                break;
//            case "Date":
//                Date d = ((DateTimeField) f).getDate();
//                mm.setDate(d);
//                break;
//            case "Subject":
//                mm.setSubject(f.getBody());
//                break;
//            case "To":
//                AddressListField dd2 = ((AddressListField) f);
//                List<MailMessageAddress> tos = new ArrayList<MailMessageAddress>();
//
//                for (Address l : dd2.getAddressList()) {
//                    MailMessageAddress mailMessageAddress2 = new MailMessageAddress();
//                    mailMessageAddress2.setEmail(l.toString());
//                    tos.add(mailMessageAddress2);
//                }
//
//                mm.setTo(tos);
//                break;
//            default:
//                System.out.println("header: " + f);
//
//            }
//
//        }
//    }
// }
