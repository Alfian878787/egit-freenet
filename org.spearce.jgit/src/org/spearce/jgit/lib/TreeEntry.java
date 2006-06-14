package org.spearce.jgit.lib;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public abstract class TreeEntry
{
    public static final int MODIFIED_ONLY = 1 << 0;

    public static final int LOADED_ONLY = 1 << 1;

    private Tree parent;

    private final byte[] nameUTF8;

    private ObjectId id;

    protected TreeEntry(
        final Tree myParent,
        final ObjectId myId,
        final byte[] myNameUTF8)
    {
        parent = myParent;
        id = myId;
        nameUTF8 = myNameUTF8;
    }

    public Tree getParent()
    {
        return parent;
    }

    public void detachParent()
    {
        parent = null;
    }

    public Repository getDatabase()
    {
        return getParent().getDatabase();
    }

    public byte[] getNameUTF8()
    {
        return nameUTF8;
    }

    public String getName()
    {
        try
        {
            return nameUTF8 != null ? new String(
                nameUTF8,
                Constants.CHARACTER_ENCODING) : null;
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new RuntimeException("JVM doesn't support "
                + Constants.CHARACTER_ENCODING, uee);
        }
    }

    public boolean isModified()
    {
        return getId() == null;
    }

    public void setModified()
    {
        setId(null);
    }

    public ObjectId getId()
    {
        return id;
    }

    public void setId(final ObjectId n)
    {
        // If we have a parent and our id is being cleared or changed then force
        // the parent's id to become unset as it depends on our id.
        //
        final Tree p = getParent();
        if (p != null && id != n)
        {
            if ((id == null && n != null)
                || (id != null && n == null)
                || !id.equals(n))
            {
                p.setId(null);
            }
        }

        id = n;
    }

    public String getFullName()
    {
        final StringBuffer r = new StringBuffer();
        appendFullName(r);
        return r.toString();
    }

    public void accept(final TreeVisitor tv) throws IOException
    {
        accept(tv, 0);
    }

    public abstract void accept(TreeVisitor tv, int flags) throws IOException;

    public abstract FileMode getMode();

    private void appendFullName(final StringBuffer r)
    {
        final TreeEntry p = getParent();
        final String n = getName();
        if (p != null)
        {
            p.appendFullName(r);
            if (r.length() > 0)
            {
                r.append('/');
            }
        }
        if (n != null)
        {
            r.append(n);
        }
    }
}