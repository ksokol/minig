package com.icegreen.greenmail.store;

import javax.mail.Flags;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ReplaceMessageFlagsFormatCallAspect {

    @Around("execution(* com.icegreen.greenmail.store.MessageFlags.format(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Flags flags = (Flags) pjp.getArgs()[0];
        return PatchedMessageFlags.format(flags);
    }
}
