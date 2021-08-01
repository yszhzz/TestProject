package cn.mypro.swing.util.file;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class WindowClipboardUtil {

    public static void setIntoClipboard(String data) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(data), null);
    }

    public static String getFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    public static void main(String[] args) {
        WindowClipboardUtil.setIntoClipboard("888");
        System.out.println(WindowClipboardUtil.getFromClipboard());


    }

}
