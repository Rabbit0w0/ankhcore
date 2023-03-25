package org.inksnow.ankh.core.api.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.FileTime;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;

public class DelegateJarFile extends JarFile {
  private final List<JarFile> delegateList;
  private JarFile[] delegates;

  public DelegateJarFile(File file, JarFile... delegates) throws IOException {
    super(file);
    this.delegateList = new ArrayList<>(delegates.length);
    this.delegateList.addAll(Arrays.asList(delegates));
    this.delegates = delegateList.toArray(new JarFile[0]);
  }

  public boolean add(JarFile jarFile) {
    boolean result = delegateList.add(jarFile);
    this.delegates = delegateList.toArray(new JarFile[0]);
    return result;
  }

  public boolean remove(JarFile jarFile) {
    boolean result = delegateList.remove(jarFile);
    this.delegates = delegateList.toArray(new JarFile[0]);
    return result;
  }

  public List<JarFile> getDelegateList() {
    return Collections.unmodifiableList(Arrays.asList(this.delegates));
  }

  @Override
  public ZipEntry getEntry(String name) {
    JarFile[] delegates = this.delegates;
    boolean failure = false;
    Exception[] exceptions = new Exception[delegates.length];
    for (int i = 0; i < delegates.length; i++) {
      try {
        ZipEntry zipEntry = delegates[i].getEntry(name);
        if (zipEntry != null) {
          return new DelegateJarEntry((JarEntry) zipEntry, delegates[i]);
        }
      } catch (Exception e) {
        failure = true;
        exceptions[i] = e;
      }
    }
    if (!failure) {
      return null;
    }
    IllegalStateException e = new IllegalStateException("Failed to getEntry in delegate jar file");
    for (Exception exception : exceptions) {
      if (exception != null) {
        e.addSuppressed(exception);
      }
    }
    throw e;
  }

  @Override
  public Enumeration<JarEntry> entries() {
    return new DelegateEnumeration(this.delegates);
  }

  @Override
  public Stream<JarEntry> stream() {
    Enumeration<JarEntry> entries = entries();
    return StreamSupport.stream(
      Spliterators.spliteratorUnknownSize(
        new Iterator<JarEntry>() {
          @Override
          public boolean hasNext() {
            return entries.hasMoreElements();
          }

          @Override
          public JarEntry next() {
            return entries.nextElement();
          }
        }, Spliterator.ORDERED
      ), false
    );
  }

  @Override
  public InputStream getInputStream(ZipEntry ze) throws IOException {
    DelegateJarEntry jarEntry = (DelegateJarEntry) ze;
    return jarEntry.getDelegateJarFile().getInputStream(
      jarEntry.getDelegateJarEntry()
    );
  }

  @Override
  public int size() {
    JarFile[] delegates = this.delegates;
    int sizeCount = 0;
    for (JarFile delegate : delegates) {
      try {
        sizeCount += delegate.size();
      } catch (IllegalStateException e) {
        //
      }
    }
    return Math.max(sizeCount, 0);
  }

  @Override
  public void close() throws IOException {
    JarFile[] delegates = this.delegates;
    Exception[] exceptions = new Exception[delegates.length + 1];
    for (int i = -1; i < delegates.length; i++) {
      try {
        if (i == -1) {
          super.close();
        } else {
          delegates[i].close();
        }
      } catch (Exception e) {
        exceptions[i] = e;
      }
    }
    IOException e = new IOException("Failed to close delegate jar file");
    for (Exception exception : exceptions) {
      if (exception != null) {
        e.addSuppressed(exception);
      }
    }
    if (e.getSuppressed().length != 0) {
      throw e;
    }
  }

  private static class DelegateEnumeration implements Enumeration<JarEntry> {
    private final JarFile[] delegates;
    private int i = 0;
    private Enumeration<JarEntry> delegate;

    public DelegateEnumeration(JarFile[] delegates) {
      this.delegates = delegates;
    }

    @Override
    public boolean hasMoreElements() {
      if (i >= delegates.length) {
        return false;
      }
      return toNextElement();
    }

    @Override
    public JarEntry nextElement() {
      toNextElement();
      return delegate.nextElement();
    }

    private boolean toNextElement() {
      while (delegate.hasMoreElements()) {
        i++;
        if (i >= delegates.length) {
          return false;
        }
        delegate = delegates[++i].entries();
      }
      return true;
    }
  }

  private static class DelegateJarEntry extends JarEntry {
    private final JarEntry jarEntry;
    private final JarFile jarFile;

    public DelegateJarEntry(JarEntry jarEntry, JarFile jarFile) {
      super(jarEntry);
      this.jarEntry = jarEntry;
      this.jarFile = jarFile;
    }

    public JarEntry getDelegateJarEntry() {
      return jarEntry;
    }

    public JarFile getDelegateJarFile() {
      return jarFile;
    }

    @Override
    public Attributes getAttributes() throws IOException {
      return jarEntry.getAttributes();
    }

    @Override
    public Certificate[] getCertificates() {
      return jarEntry.getCertificates();
    }

    @Override
    public CodeSigner[] getCodeSigners() {
      return jarEntry.getCodeSigners();
    }

    @Override
    public String getName() {
      return jarEntry.getName();
    }

    @Override
    public long getTime() {
      return jarEntry.getTime();
    }

    @Override
    public void setTime(long time) {
      jarEntry.setTime(time);
    }

    @Override
    public ZipEntry setLastModifiedTime(FileTime time) {
      return jarEntry.setLastModifiedTime(time);
    }

    @Override
    public FileTime getLastModifiedTime() {
      return jarEntry.getLastModifiedTime();
    }

    @Override
    public ZipEntry setLastAccessTime(FileTime time) {
      return jarEntry.setLastAccessTime(time);
    }

    @Override
    public FileTime getLastAccessTime() {
      return jarEntry.getLastAccessTime();
    }

    @Override
    public ZipEntry setCreationTime(FileTime time) {
      return jarEntry.setCreationTime(time);
    }

    @Override
    public FileTime getCreationTime() {
      return jarEntry.getCreationTime();
    }

    @Override
    public long getSize() {
      return jarEntry.getSize();
    }

    @Override
    public void setSize(long size) {
      jarEntry.setSize(size);
    }

    @Override
    public long getCompressedSize() {
      return jarEntry.getCompressedSize();
    }

    @Override
    public void setCompressedSize(long csize) {
      jarEntry.setCompressedSize(csize);
    }

    @Override
    public long getCrc() {
      return jarEntry.getCrc();
    }

    @Override
    public void setCrc(long crc) {
      jarEntry.setCrc(crc);
    }

    @Override
    public int getMethod() {
      return jarEntry.getMethod();
    }

    @Override
    public void setMethod(int method) {
      jarEntry.setMethod(method);
    }

    @Override
    public byte[] getExtra() {
      return jarEntry.getExtra();
    }

    @Override
    public void setExtra(byte[] extra) {
      jarEntry.setExtra(extra);
    }

    @Override
    public String getComment() {
      return jarEntry.getComment();
    }

    @Override
    public void setComment(String comment) {
      jarEntry.setComment(comment);
    }

    @Override
    public boolean isDirectory() {
      return jarEntry.isDirectory();
    }
  }
}
