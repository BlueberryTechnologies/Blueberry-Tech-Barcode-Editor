/*
 * NOT FULLY COMMENTED YET!
 * Kiwi Barcode Editor
 * Barcode Generator Class
 * 
 * This class is the main generator for printing and generating codes.
 * 
 * 
 * Last Date Modified: 01/25/2025
 * Last User Modified: gh/rileyrichard
 * License: GPL-3.0
 */
package com.blueberrytech.barcode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.print.Doc;

import java.awt.print.PrinterJob;
import java.util.List;
import java.util.ArrayList;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

/*
 * ZXING Imports
 */

 import com.google.zxing.BarcodeFormat;
 import com.google.zxing.aztec.AztecWriter;
 import com.google.zxing.client.j2se.MatrixToImageWriter;
 import com.google.zxing.oned.Code128Writer;
 import com.google.zxing.qrcode.QRCodeWriter;
 import com.google.zxing.common.BitMatrix;

public class BarcodeGenerator{
    



    String finalPath = "";
    static String targetFile;
    static String customLocation;
    static boolean selectedImage = false;
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static final File user_home = new File(System.getProperty("user.home"));
    static File defaultDirectoryToWrite = new File("");
    static PrintService[] printService;
    private static Path generatedPath;
    private static boolean isCanceled;

    private static int codeHeight = 200;
    private static int codeWidth = 200;

    public void setInitialPrinter(){
        printService = FindPrintService(PrintRequestAttributeSet());
    }

    public void updatePrinter(String printer){
        AttributeSet aset = new HashAttributeSet();
        aset.add(new PrinterName(printer, null));
        printService = PrintServiceLookup.lookupPrintServices(null, aset);
        JOptionPane.showMessageDialog(null,"Updated printer to: " + printer, "Success...",JOptionPane.INFORMATION_MESSAGE);
    }

    public void GenerateBarcodes(String text, String algo){
        /*
         * This method will take user input in the form of a text field and convert that to various barcodes or codes.
         * This method will also take two parameters:
         *              -> String
         *              -> String
         */
        String checkedText = text;
        
        try{
            File barcodeImages = new File(getDirectory(), "/barcode_images"); // Establishes a directory to save the barcode photos to.
            if (!barcodeImages.exists()){ // If the folder doesn't exist in the specified directory, then it creates one.
                barcodeImages.mkdirs();
            }
            System.out.println("Compliance: " + checkIfStringIsLink(checkedText));
            String path = barcodeImages + "/" + checkIfStringIsLink(checkedText) + ".png"; // The path that is set for every barcode created.
            
            /*
             * If algorithm is set as different, then it will generate a different barcode.
             */
            if (algo.equals("CODE128")){
                Code128Writer code128Writer = new Code128Writer();
                BitMatrix code128Matrix = code128Writer.encode(text, BarcodeFormat.CODE_128, getCodeWidth(), getCodeHeight());
                MatrixToImageWriter.writeToPath(code128Matrix, "jpg", Paths.get(path));
                setGeneratedPath(Paths.get(path));
                finalPath = path;
                JOptionPane.showMessageDialog(null,"Code Created at:\n" + getGeneratedPath(), "Success...",JOptionPane.INFORMATION_MESSAGE);
            }else if (algo.equals("AZTEC")){
                AztecWriter aztecWriter = new AztecWriter();
                BitMatrix aztecMatrix = aztecWriter.encode(text, BarcodeFormat.AZTEC, getCodeWidth(), getCodeHeight());
                MatrixToImageWriter.writeToPath(aztecMatrix, "jpg", Paths.get(path));
                setGeneratedPath(Paths.get(path));
                finalPath = path;
                JOptionPane.showMessageDialog(null,"Code Created at:\n" + getGeneratedPath(), "Success...",JOptionPane.INFORMATION_MESSAGE);
                System.out.println("The generated path is: " + getGeneratedPath());               //JOptionPane.showMessageDialog(null,"AZTEC Code Created...", "Success...",JOptionPane.WARNING_MESSAGE);
            }else if (algo.equals("QR Codes")){
                QRCodeWriter qrWriter = new QRCodeWriter(); 
                BitMatrix qrMatrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, getCodeWidth(), getCodeHeight());
                MatrixToImageWriter.writeToPath(qrMatrix, "jpg", Paths.get(path));
                setGeneratedPath(Paths.get(path));
                finalPath = path;
                JOptionPane.showMessageDialog(null,"Code Created at:\n" + getGeneratedPath(), "Success...",JOptionPane.INFORMATION_MESSAGE);
                System.out.println("The generated path is: " + getGeneratedPath());
            }else if (algo.equals("PLAIN TEXT")){
                PrintBarcode(text, true);
            }else if (algo.equals("IMAGE")){
                JOptionPane.showMessageDialog(null,"How did you get here? Interesting...", "Aborting...",JOptionPane.ERROR_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(null,"Barcode Failed", "Aborting...",JOptionPane.ERROR_MESSAGE);
            }
            if(algo.equals("PLAIN TEXT") || !algo.equals("IMAGE")){
                // This is a holder because the algo check doesnt work right.
            }else{
                System.out.println("");
            }
        }catch(Exception e){
            System.out.println("An exception has been thrown:\n> " + e.getMessage());
            JOptionPane.showMessageDialog(null,"There has been an error, please check the console.\n" + e.getMessage(), "Aborting...",JOptionPane.ERROR_MESSAGE);
        }
        
    }

    public void PrintBarcode(String path, boolean plainText){
        System.out.println(printService);
        try{
            if(plainText){
                DocFlavor textFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                PrintRequestAttributeSet().add(new Copies(1));
                String printerName = "";
                if (printService.length == 0){ // If the print service is zero then throws exception.
                throw new RuntimeException("No printer services available.");
                }
                PrintService ps = printService[0];
                DocPrintJob job = ps.createPrintJob(); // Creates a printing job to print to
                String text = path;    // These are for printing with text
                byte[] bytes = text.getBytes("UTF-8");
                Doc doc = new SimpleDoc(bytes, textFlavor, null);
                job.print(doc, PrintRequestAttributeSet()); // Initiates the printing job with the selected parameters
                System.out.println("Printing Finished.");
                JOptionPane.showMessageDialog(null, "Text was printed to:\n" + returnPrinterName(), "Success...", JOptionPane.INFORMATION_MESSAGE);
            }else if(!plainText){
                DocFlavor imageFlavor = DocFlavor.INPUT_STREAM.GIF; // Format for images.
                PrintRequestAttributeSet().add(new Copies(1));
                if (printService.length == 0){ // If the print service is zero then throws exception.
                throw new RuntimeException("No printer services available.");
                }
                PrintService ps = printService[0];
                DocPrintJob job = ps.createPrintJob(); // Creates a printing job to print to
                FileInputStream fin = new FileInputStream(path);
                Doc doc = new SimpleDoc(fin, imageFlavor, null);
                job.print(doc, PrintRequestAttributeSet()); // Initiates the printing job with the selected parameters
                fin.close(); // Closes the file input stream once it's done
                System.out.println("Printing Finished.");
                JOptionPane.showMessageDialog(null, "A code was printed to:\n" + returnPrinterName(), "Success...", JOptionPane.WARNING_MESSAGE);
            }
        }catch(Exception e){
            System.out.println("An exception has been thrown:\n> " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Aborting...", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    public PrintService[] FindPrintService(PrintRequestAttributeSet printRequestAttributeSet){
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.GIF, printRequestAttributeSet);
        return printService;
    }
    private static void setGeneratedPath(Path path){
        String pathString = path.toString();
        pathString = pathString.replace(" ", "");
        generatedPath = Paths.get(pathString);
    }
    public static Path getGeneratedPath(){
        System.out.println("Path:" + generatedPath);
        return generatedPath;
    }

    public boolean checkIfTextValid(String text){
        if (text.matches("[a-zA-Z_0-9/:.?=-]+")){
            return true;
        }else{
            return false;
        }
    }
    
    public PrintRequestAttributeSet PrintRequestAttributeSet(){
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        return printRequestAttributeSet;
    }

    public static String returnPrinterName(){
        //PrintRequestAttributeSet().add(new Copies(1));
        if (printService.length == 0){ // If the print service is zero then throws exception.
            JOptionPane.showMessageDialog(null,"No printer services available", "Aborting...",JOptionPane.WARNING_MESSAGE);
            return "No Printer Found";
        }
        PrintService ps = printService[0];
        return ps.getName();
    }
    private String checkIfStringIsLink(String link){
        if (link.contains(".")){
            link = link.replace(".", "");
        }
        if (link.contains("/")){
            link = link.replace("/", "");
        }
        if (link.contains(":")){
            link = link.replace(":", "");
        }
        if (link.contains("?")){
            link = link.replace("?", "");
        }
        if (link.contains("=")){
            link = link.replace("=", "");
        }
        if (link.contains("-")){
            link = link.replace("-", "");
        } // What the fuck is this shit, idk if I should fix it
        return link;
    }
    public void selectCodeImage(){
        String updatedImagePath = getImageSavePath() + "/barcode_images";
        File barcodeImages = new File(getImageSavePath(), "/barcode_images"); // Establishes a directory to save the barcode photos to.
        if (!barcodeImages.exists()){ // If the folder doesn't exist in the specified directory, then it creates one.
            barcodeImages.mkdirs();
        }
        JFileChooser jfc = new JFileChooser(updatedImagePath);
        int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			System.out.println(selectedFile.getAbsolutePath());
            setImageFile(selectedFile.getAbsolutePath());
            selectedImage = true;
            setCanceledImage(false);
		}else{
            selectedImage = false;
            setCanceledImage(true);
            JOptionPane.showMessageDialog(null,"You canceled image selection.", "Notify...",JOptionPane.CANCEL_OPTION);
        }
    }
    public void selectImage(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			System.out.println(selectedFile.getAbsolutePath());
            setImageFile(selectedFile.getAbsolutePath());
            selectedImage = true;
            setCanceledImage(false);
		}else{
            selectedImage = false;
            setCanceledImage(true);
            JOptionPane.showMessageDialog(null,"You canceled image selection.", "Notify...",JOptionPane.CANCEL_OPTION);
        }
    }
    
    private static void setCanceledImage(boolean isCanceledLocal){
        isCanceled = isCanceledLocal;
    }
    public boolean getCanceledImage(){
        return isCanceled;
    }

    public static void setImageFile(String fileThatWasSelected){
        targetFile = fileThatWasSelected;
    }
    public static String getImageFile(){
        return targetFile;
    }
    public String getReturnPath(){
        return finalPath;
    }
    public static String getImageSavePath(){
        return getDirectory();
    }
    public String getCurrPrinter(){
        String printer = printService.toString();
        return printer;
    }


    /*
     * Setting and getting directories
     */
    public static String getDirectory(){
        try{
            if (getDefaultDirectory() == null){

            }
            File fileToReadFrom = new File(getDefaultDirectory() + "/customPath.txt");
            if (!fileToReadFrom.exists()){
                setDirectory(true);
                JOptionPane.showMessageDialog(null, "A Custom File Text has been generated and placed in: " + getDefaultDirectory(), "Notify...", JOptionPane.INFORMATION_MESSAGE);
            }
            FileReader readingFile = new FileReader(fileToReadFrom);
            Scanner readingFileScanner = new Scanner(readingFile);
            String data = readingFileScanner.nextLine();
            readingFileScanner.close();
            return data;
        }catch(FileNotFoundException e){
            System.out.println(e);
        }
        return null;
    }
    public static File getDefaultDirectory(){
        setDefaultDirectory();
        return defaultDirectoryToWrite;
    }
    private static void setDefaultDirectory(){
        File defaultDirectory = new File("");
        if (OS.contains("windows") || OS.contains("win")){
            defaultDirectory = new File(user_home + "\\Documents", "Kiwi");
            if(!defaultDirectory.exists()){
                JOptionPane.showMessageDialog(null, "The default directory doesn't exist.\nIt has been created.", "Warning", JOptionPane.INFORMATION_MESSAGE);
                defaultDirectory.mkdirs();
            }
        }else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")){ // Checks if the operating system is a Linux Variant.
            defaultDirectory = new File(user_home, "Kiwi");
            if(!defaultDirectory.exists()){
                JOptionPane.showMessageDialog(null, "The default directory doesn't exist.\nIt has been created.", "Warning", JOptionPane.INFORMATION_MESSAGE);
                defaultDirectory.mkdirs();
            }
        }else if (OS.contains("mac")){ // Checks if the operating system is MacOS
            defaultDirectory = new File(user_home, "Kiwi");
            if(!defaultDirectory.exists()){
                JOptionPane.showMessageDialog(null, "The default directory doesn't exist.\nIt has been created.", "Warning", JOptionPane.INFORMATION_MESSAGE);
                defaultDirectory.mkdirs();
            }
        }else {
            JOptionPane.showMessageDialog(null,"Operating System Not Found.\nThe program will exit now, sorry for the inconvenience.", "Aborting...",JOptionPane.WARNING_MESSAGE); 
        }
        defaultDirectoryToWrite = defaultDirectory;
    }
    public static File setDirectory(boolean isDefault){
            if (!isDefault){
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    customLocation = selectedFile.getAbsolutePath();
                    try{
                        FileWriter writingFile = new FileWriter(getDefaultDirectory() + "/customPath.txt");
                        writingFile.write(customLocation);
                        writingFile.close();
                        return selectedFile;
                    }catch(IOException e){
                        JOptionPane.showMessageDialog(null,"There was an error setting the custom directory.\nPlease check the console.", "Aborting...",JOptionPane.ERROR_MESSAGE); 
                        System.out.println("There was an error setting the custom directory.\n> " + e.getMessage());
                    }
                }
            }else{
                try{
                    customLocation = getDefaultDirectory().getAbsolutePath();
                    FileWriter writingFile = new FileWriter(getDefaultDirectory() + "/customPath.txt");
                    writingFile.write(customLocation);
                    writingFile.close();
                    return getDefaultDirectory();
                }catch(IOException e){
                    JOptionPane.showMessageDialog(null,"There was an error setting the default directory.\nPlease check the console.", "Aborting...",JOptionPane.ERROR_MESSAGE); 
                    System.out.println("There was an error setting the default directory.\n> " + e.getMessage());
                }
            }
            return null;
    }

    /*
     * Specifying the printer to print to
     * https://stackoverflow.com/questions/14885993/how-do-i-specify-the-printer-i-want-to-use-in-java
     * It's the second result
     */

     public static PrintService findPrintService(String printerName){
        printerName = printerName.toLowerCase();

        PrintService service = null;
        PrintService[] services = PrinterJob.lookupPrintServices();

        for (int index = 0; service == null && index < services.length; index++) {

            if (services[index].getName().toLowerCase().indexOf(printerName) >= 0) {
                service = services[index];
            }
        }
        return service;
     }
     public static List<String> getPrinterServiceNameList() {

        // get list of all print services
        
        PrintService[] services = PrinterJob.lookupPrintServices();

        List<String> list = new ArrayList<String>();

        for (int i = 0; i < services.length; i++) {
            list.add(services[i].getName());
        }
        return list;
    }

    /*
     * 
     * Code Dimensions
     * 
     */

    /*
     * Writing code dimensions to file.
     */

    public static void writeCodeDimension(int height, int width){
        // Finding existing directory.
        File dimensionTxt = new File(getDefaultDirectory() + "/dimension.txt");

        // If the file doesn't exist with a dimension then make one and fill it in.
        if (!dimensionTxt.exists()){
            setDefaultDimensions();
        }else{
            try {
                FileWriter writingToDimension = new FileWriter(dimensionTxt);
                writingToDimension.write(height + "x" + width);
                writingToDimension.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setDefaultDimensions(){
        FileWriter writeCodeDimension;
        File dimensionTxt = new File(getDefaultDirectory() + "/dimension.txt");
        try {
            if (!dimensionTxt.exists()){
                writeCodeDimension = new FileWriter(dimensionTxt);
                writeCodeDimension.write("200x200");
                writeCodeDimension.close();
                JOptionPane.showMessageDialog(null, "A default code dimension was not found.\nIt has been created.", "Alert...", JOptionPane.INFORMATION_MESSAGE);
            }else{
                System.out.println("The default dimension exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setCodeDimensions(int height, int width){
        writeCodeDimension(height, width);
        codeHeight = height;
        codeWidth = width;
    }
    public static int getCodeHeight(){
        return codeHeight;
    }
    public static int getCodeWidth(){
        return codeWidth;
    }
}