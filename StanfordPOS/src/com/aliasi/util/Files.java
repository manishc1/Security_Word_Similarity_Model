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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Static utility methods for processing files.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 */
public class Files {

    /**
     * Forbid instance construction.
     */
    private Files() {
        /* do nothing */
    }

    /**
     * Writes the specified bytes to the specified file.
     *
     * @param bytes Bytes to write to file.
     * @param file File to which characters are written.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static void writeBytesToFile(byte[] bytes, File file)
        throws IOException {

        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        Streams.closeOutputStream(out);
    }

    /**
     * Returns the array of bytes read from the specified file.
     *
     * @param file File from which to read bytes.
     * @return Bytes read from the specified file.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static byte[] readBytesFromFile(File file)
        throws IOException {

        long fileLength = file.length();
        if (fileLength > Integer.MAX_VALUE) {
            String msg = "Files must be less than Integer.MAX_VALUE=" + Integer.MAX_VALUE
                + " in length."
                + " Found file.length()=" + file.length();
            throw new IllegalArgumentException(msg);
        }

        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream((int)fileLength);
        Streams.copy(in,bytesOut);
        Streams.closeInputStream(in);
        return bytesOut.toByteArray();
    }

    /**
     * Writes the characters to the specified file, encoded
     * using the specified character set.
     *
     * @param chars Characters to write to file.
     * @param file File to which characters are written.
     * @param encoding Character encoding used by file.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static void writeCharsToFile(char[] chars,
                                        File file, String encoding)
        throws IOException {

        FileOutputStream out = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(out,encoding);
        writer.write(chars);
        Streams.closeWriter(writer);
    }

    /**
     * Writes the characters to the specified file, encoded using
     * UTF-8.
     *
     * @param chars Characters to write to file.
     * @param file File to which characters are written.
     * @throws IOException If there is an underlying I/O exception.
     * @deprecated Use {@code writeCharsToFile(chars,file,Strings.UTF8)} instead.
     */
    @Deprecated
    public static void writeCharsToFile(char[] chars, File file)
        throws IOException {

        writeCharsToFile(chars,file,Strings.UTF8);
    }

    /**
     * Writes the string to the specified file, encoded using UTF-8
     * Unicode.
     *
     * @param s String to write to file.
     * @param file File to which characters are written.
     * @throws IOException If there is an underlying I/O exception.
     * @deprecated Use {@code writeStringToFile(s,file,Strings.UTF8)} instead.
     */
    @Deprecated
    public static void writeStringToFile(String s, File file)
        throws IOException {

        writeCharsToFile(s.toCharArray(),file);
    }

    /**
     * Writes the string to the specified file, encoded using the
     * specified character set.
     *
     * @param s String to write to file.
     * @param file File to which characters are written.
     * @param encoding Character set to use for encoding.
     * @throws IOException If there is an underlying I/O exception.
     */
    public static void writeStringToFile(String s, File file, String encoding)
        throws IOException {

        writeCharsToFile(s.toCharArray(),file,encoding);
    }

    /**
     * Reads all of the bytes from the specified file and convert
     * them to a character array using the specified character set.
     *
     * @param file File from which to read input.
     * @param encoding Encoding to decode bytes in file.
     * @return Characters in the file.
     * @throws IOException If there is an underlying I/O exception.
     * @throws UnsupportedEncodingException If the encoding is not
     * supported.
     * @throws IllegalArgumentException If the file is longer than
     * the maximum integer value.
     */
    public static char[] readCharsFromFile(File file, String encoding)
        throws IOException {

        long fileLength = file.length();
        if (fileLength > Integer.MAX_VALUE) {
            String msg = "Files must be less than Integer.MAX_VALUE=" + Integer.MAX_VALUE
                + " in length."
                + " Found file.length()=" + file.length();
            throw new IllegalArgumentException(msg);
        }
        CharArrayWriter charWriter = new CharArrayWriter((int)fileLength); // may be bigger
        FileInputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader bufferedReader = null;
        try {
            in = new FileInputStream(file);
            inReader = new InputStreamReader(in,encoding);
            bufferedReader = new BufferedReader(inReader);
            Streams.copy(bufferedReader,charWriter);
        } finally {
            Streams.closeReader(bufferedReader);
            Streams.closeReader(inReader);
            Streams.closeInputStream(in);
        }
        return charWriter.toCharArray();
    }

    /**
     * Reads all of the bytes from the specified file and convert
     * them to a character array using UTF-8 unicode.
     *
     * @param file File from which to read input.
     * @return Characters in the file.
     * @throws IOException If there is an underlying I/O exception.
     * @deprecated Use {@code readCharsFromFile(file,Strings.UTF8)} instead.
     */
    @Deprecated
    public static char[] readCharsFromFile(File file)
        throws IOException {

        return readCharsFromFile(file,Strings.UTF8);
    }

    /**
     * Reads all of the bytes from the specified file and convert
     * them to a string using the specified character set.
     *
     * @param file File from which to read input.
     * @param encoding Encoding to decode bytes in file.
     * @return Characters in the file.
     * @throws IOException If there is an underlying I/O exception.
     * @throws UnsupportedEncodingException If the encoding is not supported.
     */
    public static String readFromFile(File file, String encoding)
        throws IOException {

        return new String(readCharsFromFile(file,encoding));

    }

    /**
     * Reads all of the bytes from the specified file and convert
     * them to a string using Unicode UTF-8.
     *
     * @param file File from which to read input.
     * @return Characters in the file.
     * @throws IOException If there is an underlying I/O exception.
     * @deprecated Use {@code readFromFile(file,Strings.UTF8)} instead.
     */
    @Deprecated
    public static String readFromFile(File file) throws IOException {

        return readFromFile(file,Strings.UTF8);
    }


    /**
     * Reads a serialized object from the specified file.
     *
     * @param file File from which to read object.
     * @return Object read from file.
     * @throws IOException If there is an I/O error reading.
     * @throws ClassNotFoundException If the serialized object's class
     * is not on the classpath.
     * @deprecated Use {@code
     * com.aliasi.util.AbstractExteranlizable.readObject(File)}
     * instead.
     */
    @Deprecated
    public static Object readObjectFrom(File file)
        throws ClassNotFoundException, IOException {

        FileInputStream fileIn = null;
        BufferedInputStream bufIn = null;
        ObjectInputStream objIn = null;
        try {
            fileIn = new FileInputStream(file);
            bufIn = new BufferedInputStream(fileIn);
            objIn = new ObjectInputStream(bufIn);
            return objIn.readObject();
        } finally {
            Streams.closeInputStream(objIn);
            Streams.closeInputStream(bufIn);
            Streams.closeInputStream(fileIn);
        }
    }

    /**
     * Writes the specified object to the specified file.
     *
     * @param object Object to write.
     * @param file File to which object is serialized.
     * @throws IOException If there is an underlying I/O error
     * writing.
     */
    public static void writeObjectTo(Serializable object, File file)
        throws IOException {

        FileOutputStream fileOut = null;
        BufferedOutputStream bufOut = null;
        ObjectOutputStream objOut = null;
        try {
            fileOut = new FileOutputStream(file);
            bufOut = new BufferedOutputStream(fileOut);
            objOut = new ObjectOutputStream(bufOut);
            objOut.writeObject(object);
        } finally {
            Streams.closeOutputStream(objOut);
            Streams.closeOutputStream(bufOut);
            Streams.closeOutputStream(fileOut);
        }
    }

    /**
     * Returns the name of a file before the final period separator.
     *
     * @param name Name of file.
     * @return File name's prefix.
     * @deprecated Only used as part of {@link #baseName(File)}.
     */
    @Deprecated
    public static String prefix(String name) {
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex < 0) return name;
        return name.substring(0,lastDotIndex);
    }


    /**
     * Returns the name of a file after the final period separator.
     *
     * @param name Name of file.
     * @return File name's suffix.
     * @deprecated Only used as part of {@link #extension(File)}.
     */
    @Deprecated
    public static String suffix(String name) {
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex < 0) return "";
        if (lastDotIndex >= name.length()) return "";
        return name.substring(lastDotIndex+1);
    }


    /**
     * Returns prefix of the file's name, defined as the
     * part of the name before the final period, or the
     * whole name if there is no final period.
     *
     * @param file File whose name's prefix is returned.
     * @return Prefix of file.
     */
    public static String baseName(File file) {
        return prefix(file.getName());
    }

    /**
     * Returns the suffix of the file's name, defined
     * as the part of the name after the final period,
     * or {@code null} if there is no period in the name.
     *
     * @return The file name's extension.
     */
    public static String extension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex < 0) return null;
        return name.substring(lastDotIndex+1);
    }

    /**
     * Removes the specified file and if it is a directory, all
     * contained files.  Returns number of files removed, including
     * specified one.
     *
     * @param file File or directory to remove.
     * @return Number of files and directories removed.
     */
    public static int removeRecursive(File file) {
        if (file == null) return 0; // nothing to remove
        int descCount = removeDescendants(file);
        file.delete();
        return descCount + 1;
    }

    /**
     * Remove the descendants of the specified directory, but not the
     * directory itself.  Returns number of files removed.
     *
     * @param file File whose descendants are removed.
     * @return Number of files or directories removed.
     */
    public static int removeDescendants(File file) {
        if (!file.isDirectory()) return 0;
        int count = 0;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; ++i)
            count += removeRecursive(files[i]);
        return count;
    }

    /**
     * Creates a clean directory with the specified name
     * as a subdirectory of the specified directory.
     *
     * @param directory Parent directory of directory to create.
     * @param name Name of directory to create.
     * @return Directory with specified name that is daughter of
     * specified parent directory.
     * @deprecated Implement with compound statements in {@code java.io.File}.
     */
    @Deprecated
    public static File makeCleanDir(File directory, String name) {
        return makeCleanDir(new File(directory,name));
    }

    /**
     * Creates a directory in the specified file that has no
     * subdirectories.  If the specified file exists as a file, it
     * is removed first.  If the specified file is a directory with
     * files or subdireictories, they are removed.
     *
     * @param file File to create as a clean directory.
     * @return Directory with specified name that is daughter of
     * specified parent directory.
     * @deprecated Implement with compound statements in {@code java.io.File}.
     */
    @Deprecated
    public static File makeCleanDir(File file) {
        if (file.exists() & !file.isDirectory()) file.delete();
        if (!file.isDirectory()) file.mkdir();
        removeDescendants(file);
        return file;
    }

    /**
     * Returns a new file with the specified name that is a daughter
     * of the temporary directory {@link #TEMP_DIRECTORY}.  The file
     * will be deleted automicatically when the virtual machine exits.
     *
     * @param fileName Name of file to create.
     * @return Temporary file with specified name.
     * @deprecated Use {@code java.io.File.createTempFile(String,String)} instead.
     */
    @Deprecated
    public static File createTempFile(String fileName) {
        File tempFile = new File(TEMP_DIRECTORY,fileName);
        tempFile.deleteOnExit();
        return tempFile;
    }

    /**
     * Converts a file to the string representation of its URL,
     * consisting of the URL prefix and a canonical path name.
     *
     * <p><b>Warning:</b> This method is deprecated because it does
     * not properly escape illegal URL characters.
     *
     * @param file File to convert to string URL.
     * @return The string URL corresponding to the specified file.
     * @throws IOException If there is an exception retrieving the
     * file's canonical path name.
     * @deprecated Instead of {@code Files.fileToURLName(file)} use
     * {@code file.toURI().toURL().toString()}.
     */
    @Deprecated
    public static String fileToURLName(File file) throws IOException {
        return FILE_URL_PREFIX + file.getCanonicalPath();
    }

    /**
     * Returns the lines of a file as an array of strings.  The
     * newline characters are removed.
     *
     * @param file Name of file from which to read lines.
     * @param encoding Character set to use to read lines.
     * @return Array of lines read from file.
     * @throws IOException If there is an I/O exception opening,
     * closing, or reading from the file.
     * @deprecated Use {@link com.aliasi.io.FileLineReader#readLineArray(File,String)} instead.
     */
    @Deprecated
    public static String[] readLinesFromFile(File file, String encoding)
        throws IOException {

        FileInputStream in = null;
        InputStreamReader streamReader = null;
        BufferedReader reader = null;

        try {
            in = new FileInputStream(file);
            streamReader = new InputStreamReader(in,encoding);
            reader = new BufferedReader(streamReader);
            List<String> list = new ArrayList<String>();
            String line;
            while ((line= reader.readLine()) != null) {
                list.add(line);
            }
            return list.<String>toArray(Strings.EMPTY_STRING_ARRAY);
        } finally {
            Streams.closeInputStream(in);
            Streams.closeReader(streamReader);
            Streams.closeReader(reader);
        }
    }

    /**
     * Serializes the specifies object and returns its deserialized
     * version.  This operates in memory by writing to a byte-array
     * backed object output stream and reading from a byte-array
     * backed object input stream.
     *
     * @param in Object to serialize and then deserialize.
     * @return Deserialized version of serialized version of specified
     * object.
     * @throws IOException If there is an I/O error while reading or
     * writing.
     * @throws ClassNotFoundException If the class for the object
     * being restored cannot be found.
     * @deprecated Use {@code com.aliasi.util.AbstractExteranlizable.serializeDeserialize(Serializable)} instead.
     */
    @Deprecated
    public static Object serializeDeserialize(Serializable in)
        throws ClassNotFoundException, IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(bytesOut);
        objOut.writeObject(in);
        byte[] bytes = bytesOut.toByteArray();
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream objIn = new ObjectInputStream(bytesIn);
        return objIn.readObject();
    }

    /**
     * Copies the contents of one file into another.  Even if there
     * is an exception, both file handles will be closed on exit.
     *
     * @param from File from which to copy.
     * @param to File to which to copy.
     * @throws IOException If there is a read or write error.
     */
    public static void copyFile(File from, File to) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);
            byte[] bytes = new byte[1024*4];
            int len = 0;
            while ((len = in.read(bytes)) >= 0)
                out.write(bytes,0,len);
        } finally {
            Streams.closeInputStream(in);
            Streams.closeOutputStream(out);
        }
    }

    /**
     * Prefix for file names to convert them into URLs.
     */
    private static String FILE_URL_PREFIX = "file:///";

    /**
     * Name of the property for the system temporary directory.
     */
    private static final String TEMP_DIRECTORY_SYS_PROPERTY = "java.io.tmpdir";

    /**
     * The temporary directory.
     *
     * @deprecated Use {@code new
     * File(System.getProperty(TEMP_DIRECTORY_SYS_PROPERTY))} instead.
     */
    @Deprecated
    public static final File TEMP_DIRECTORY
        = new File(System.getProperty(TEMP_DIRECTORY_SYS_PROPERTY));


    /**
     * A file filter that accepts files that are directories
     * that are not named "CVS", ignoring case.
     */
    public static final FileFilter NON_CVS_DIRECTORY_FILE_FILTER
        = new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory()
                        && !file.getName().equalsIgnoreCase("CVS");
                }
            };

    /**
     * A file filter that accepts all normal files, as
     * specified by {@link File#isFile()}.
     */
    public static final FileFilter FILES_ONLY_FILE_FILTER
        = new FileFilter() {
                public boolean accept(File file) {
                    return file.isFile();
                }
            };


}
