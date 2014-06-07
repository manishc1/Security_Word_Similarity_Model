/*
 * LingPipe v. 3.9
 * Copyright (C) 2003-2010 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * Utility classes for handling reflection.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @deprecated Use {@code java.lang.reflect} directly instead, as
 * described on the individual methods.
 */
@Deprecated
public class Reflection {

    /**
     * Disallow instance construction.
     */
    private Reflection() {
        /* do nothing */
    }


    /**
     * Construct a new instance of the class of the specified name
     * with the no-argument constructor.
     *
     * @param className Name of class of which to construct an instance.
     * @throws IllegalArgumentException If there are errors in construction.
     * @deprecated Use {@code Class.forName(className).getConstructor(new Class[0]).newInstance(new Object[0])} instead.
     */
    @Deprecated
    public static Object newInstance(String className) {
        return newInstance(className, new Object[] { });
    }

    /**
     * Construct a new instance of the class of the specified name
     * with the specified arguments.
     *
     * <p>All exceptions arising from reflection are converted
     * to illegal argument exceptions as detailed in the method
     * documentation to {@link #newInstance(String,Object[],String[])}.
     *
     * @param className Name of class of which to construct an instance.
     * @param args Arguments to supply to constructor.
     * @throws IllegalArgumentException If there are errors in construction.
     * @throws ClassCastException If the class name is not of the
     * generic type.
     * @param <T> type of object constructed
     * @deprecated Convert {@code args} to {@code argClasses} using {@code args[i].getClass()},
     * then call {@code Class.forName(className).getConstructor(argClasses).newInstace(args)} instead.
     */
    @Deprecated
    public static <T> T newInstance(String className, Object[] args) {
        Class<?>[] argClasses = new Class<?>[args.length];
        for (int i = 0; i < args.length; ++i)
            argClasses[i] = args[i].getClass();
        return Reflection.<T>newInstance(className,args,argClasses);
    }

    /**
     * Construct a new instance of the class of the specified name
     * with the specified arguments and argument class names.
     *
     * <p>This method converts all exceptions thrown by {@link
     * Class#forName(String)} in converting the class names to
     * classes to illegal argument exceptions.  All other exceptions
     * documented to be thrown by reflection construction are
     * converted by {@link #newInstance(String,Object[],Class[])}.
     *
     * @param className Name of class of which to construct an instance.
     * @param args Arguments to supply to constructor.
     * @param argClassNames Classes of argument determining constructor.
     * @throws IllegalArgumentException If there are errors in construction.
     * @throws ClassCastException If the class name is not of the
     * generic type.
     * @param <T> type of object constructed
     * @deprecated Convert {@code argClassNames} to {@code argClasses}
     * classes using {@code Class.forName(argClassNames[i])}, then use
     * {@code Class.forName(className).getConstructor(argClasses).newInstace(args)} instead.
     */
    @Deprecated
    public static <T> T newInstance(String className,
                                    Object[] args,
                                    String[] argClassNames) {
        Class<?>[] argClasses = new Class<?>[argClassNames.length];
        try {
            for (int i = 0; i < argClassNames.length; ++i)
                argClasses[i] = Class.forName(argClassNames[i]);
            return Reflection.<T>newInstance(className,args,argClasses);
        } catch (ExceptionInInitializerError e) {
            throw toIllegalArgument(e);
        } catch (LinkageError e) {
            throw toIllegalArgument(e);
        } catch (ClassNotFoundException e) {
            throw toIllegalArgument(e);
        }
    }

    /**
     * Construct a new instance of the class of the specified name
     * with the specified arguments and argument classes.
     *
     * <p>All exceptions thrown by reflection are converted to illegal
     * argument exceptions; this includes exceptions thrown by {@link
     * Class#forName(String)} in getting the constructor's class, by
     * {@link Class#getConstructor(Class[])} in getting the
     * constructor itself, and by {@link
     * Constructor#newInstance(Object[])} in creating the instance.
     *
     * @param className Name of class of which to construct an instance.
     * @param args Arguments to supply to constructor.
     * @param argClasses Classes of argument determining constructor.
     * @throws IllegalArgumentException If there are errors in construction.
     * @throws ClassCastException If the class name is not of the
     * generic type.
     * @param <T> type of object constructed
     * @deprecated Use {@code Class.forName(className).getConstructor(argClasses).newInstance(args)} instead.
     */
    @Deprecated
    public static <T> T newInstance(String className,
                                    Object[] args,
                                    Class<?>[] argClasses) {
        try {
            Class<?> consClass = Class.forName(className);
            // need to use this with Class<?> return from forName()
            @SuppressWarnings("unchecked")
            Constructor<T> cons = (Constructor<T>) consClass.getConstructor(argClasses);
            return cons.newInstance(args);
        } catch (IllegalAccessException e) {
            throw toIllegalArgument(e);
        } catch (InstantiationException e) {
            throw toIllegalArgument(e);
        } catch (InvocationTargetException e) {
            throw toIllegalArgument(e);
        } catch (ExceptionInInitializerError e) {
            throw toIllegalArgument(e);
        } catch (ClassNotFoundException e) {
            throw toIllegalArgument(e);
        } catch (NoSuchMethodException e) {
            throw toIllegalArgument(e);
        }
    }

    private static IllegalArgumentException toIllegalArgument(Throwable t) {
        String msg = "Exception in Reflection.newInstance()";
        return Exceptions.toIllegalArgument(msg,t);
    }

}
