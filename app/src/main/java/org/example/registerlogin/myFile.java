package org.example.registerlogin;

public class myFile {
    private String filename;
    private String filetype;

    public myFile(String Filename, String Filetype) {
        filename = Filename;
        filetype = Filetype;
    }

    public String getName() {
        return filename;
    }

    public void setName(String Filename) {
        this.filename = Filename;
    }

    public String getType() {
        return filetype;
    }

    public void setType(String Filetype) {
        this.filetype = Filetype;
    }
}
