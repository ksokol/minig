package org.minig.server.service.impl.helper.mime;

import java.io.IOException;
import java.io.InputStream;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

public class MyContentHandler extends AbstractContentHandler {

    int pad = 0;

    @Override
    public void startMessage() throws MimeException {
        System.out.println(" startMessage");

    }

    @Override
    public void endMessage() throws MimeException {
        // TODO Auto-generated method stub
        System.out.println(" endMessage");
    }

    @Override
    public void startBodyPart() throws MimeException {
        // TODO Auto-generated method stub
        System.out.println("   startBodyPart");
    }

    @Override
    public void endBodyPart() throws MimeException {
        // TODO Auto-generated method stub
        System.out.println("   endBodyPart");
    }

    @Override
    public void startHeader() throws MimeException {
        // TODO Auto-generated method stub
        System.out.println("  startHeader");
    }

    @Override
    public void field(Field rawField) throws MimeException {
        // TODO Auto-generated method stub
        System.out.println("   field " + rawField);
    }

    @Override
    public void endHeader() throws MimeException {
        // TODO Auto-generated method stub
        System.out.println("  endHeader");
    }

    @Override
    public void preamble(InputStream is) throws MimeException, IOException {
        // TODO Auto-generated method stub
        System.out.println("preamble");
    }

    @Override
    public void epilogue(InputStream is) throws MimeException, IOException {
        // TODO Auto-generated method stub
        System.out.println("epilogue");
    }

    @Override
    public void startMultipart(BodyDescriptor bd) throws MimeException {
        // TODO Auto-generated method stub
        System.out.println("  startMultipart " + bd);
    }

    @Override
    public void endMultipart() throws MimeException {
        // TODO Auto-generated method stub
        System.out.println("  endMultipart");
    }

    @Override
    public void raw(InputStream is) throws MimeException, IOException {
        // TODO Auto-generated method stub
        // System.out.println("endMessage");
    }
}
