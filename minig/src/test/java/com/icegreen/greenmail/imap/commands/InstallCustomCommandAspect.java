package com.icegreen.greenmail.imap.commands;

import java.lang.reflect.Field;
import java.util.Map;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

/**
 * 
 * @author Kamill Sokol <dev@sokol-web.de>
 * 
 *         Greenmail does not allow to extend/replace existing ImapCommands by a
 *         custom implementation without touching the source code.
 * 
 *         To prevent a unique build of a third party library we define an
 *         aspect which replaces various commands by our own implementation.
 * 
 */
@Aspect
public class InstallCustomCommandAspect {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @AfterReturning(pointcut = "call(com.icegreen.greenmail.imap.commands.ImapCommandFactory.new(..))", returning = "result")
    public void replace(Object result) throws Throwable {
        /*
         * In case of an exception. Greenmails internal implementation changed
         * possibly
         */

        Field f = result.getClass().getDeclaredField("_imapCommands");
        f.setAccessible(true);

        Map _imapCommands = (Map) f.get(result);

        _imapCommands.put(SearchCommand.NAME, MessageIdTermSearchCommand.class);
        _imapCommands.put(FetchCommand.NAME, PatchedFetchCommand.class);
        _imapCommands.put(StoreCommand.NAME, PatchedStoreCommand.class);
    }

}