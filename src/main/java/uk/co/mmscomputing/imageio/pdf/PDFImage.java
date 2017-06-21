package uk.co.mmscomputing.imageio.pdf;

import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.awt.color.*;

import uk.co.mmscomputing.imageio.jpeg.JFIFInputStream;
import uk.co.mmscomputing.imageio.jpeg.JFIFOutputStream;

import uk.co.mmscomputing.io.RLEBit1OutputStream;
import uk.co.mmscomputing.io.ModHuffmanOutputStream;
import uk.co.mmscomputing.io.ModModREADOutputStream;
import uk.co.mmscomputing.io.BitSwapOutputStream;

import uk.co.mmscomputing.io.ModHuffmanInputStream;
import uk.co.mmscomputing.io.ModModREADInputStream;
import uk.co.mmscomputing.io.BitSwapInputStream;
import uk.co.mmscomputing.io.RLEBitInputStream;

public class PDFImage extends PDFXObject{
  
  private BufferedImage image;

  public PDFImage(String name, BufferedImage image){
    super("Image");

    int width  = image.getWidth();
    int height = image.getHeight();

    // [1] p.79 Name must match the name used in the XObject dictionary within the page's Resources dictionary

    put("Name",             new PDFObject.PDFName(name));  
    put("Width",            new PDFObject.PDFInteger(width));  
    put("Height",           new PDFObject.PDFInteger(height));  

    ColorModel cm =image.getColorModel();
    if((image.getType()==BufferedImage.TYPE_BYTE_BINARY)&&(cm.getPixelSize()==1)){

      put("BitsPerComponent", new PDFObject.PDFInteger(1));
      put("ColorSpace",       new PDFObject.PDFName("DeviceGray"));  

      setFilter("CCITTFaxDecode");

      PDFDictionary ccittDict = new PDFDictionary();                       // but for CCITTFaxDecode
//      ccittDict.put("K",new PDFObject.PDFInteger(0));                    // ModHuffmanOutputStream
      ccittDict.put("K",new PDFObject.PDFInteger(-1));                   // ModModREADOutputStream
      ccittDict.put("Columns",new PDFObject.PDFInteger(width));          
      ccittDict.put("Rows",new PDFObject.PDFInteger(height));          

      put("DecodeParms",ccittDict);  

    }else if(image.getType()==BufferedImage.TYPE_BYTE_GRAY){       // one component; grey scale
      System.out.println(getClass().getName()+"\n\tPDF JPEG GRAYSCALE\n\t"+name);

      put("BitsPerComponent", new PDFObject.PDFInteger(8));
      put("ColorSpace",       new PDFObject.PDFName("DeviceGray"));  

      String[] filters = {/*"ASCII85Decode",*/"DCTDecode"};            // /Filter /ASCII85Decode /DCTDecode
      setFilters(filters);
    }else/* if(image.getType()==BufferedImage.TYPE_INT_RGB)*/{     // three components; YCbCr
      System.out.println(getClass().getName()+"\tPDF JPEG RGB");

      if(image.getType()!=BufferedImage.TYPE_INT_RGB){
        BufferedImage  img      = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        ColorSpace     srcSpace = image.getColorModel().getColorSpace();
        ColorSpace     newSpace = img.getColorModel().getColorSpace();
        ColorConvertOp convert  = new ColorConvertOp(srcSpace,newSpace,null);
        convert.filter(image,img);
        image=img;
      }

      put("BitsPerComponent", new PDFObject.PDFInteger(8));        // only RGB for the moment
      put("ColorSpace",       new PDFObject.PDFName("DeviceRGB"));  

      String[] filters = {/*"ASCII85Decode",*/"DCTDecode"};            // /Filter /ASCII85Decode /DCTDecode
      setFilters(filters);
    }
    this.image = image;
  }

  public PDFImage(PDFDictionary dict){
    super(dict);
  }

  public BufferedImage getImage(){return image;}

  public void write(PDFFile out)throws IOException{
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    if(image.getType()==BufferedImage.TYPE_BYTE_BINARY){
      encodeCCITT(baos,image);
    }else if(image.getType()==BufferedImage.TYPE_BYTE_GRAY){
      encodeJPEG(baos,image);
    }else/* if(image.getType()==BufferedImage.TYPE_INT_RGB)*/{
      encodeJPEG(baos,image);
    }
    setInputStream(baos.toByteArray());
    super.write(out);
  }

  private void encodeCCITT(ByteArrayOutputStream  baos, BufferedImage image)throws IOException{

//      System.out.println(getClass().getName()+"\tPDF BINARY CCITTFaxDecode");

    int width  = image.getWidth();
    int height = image.getHeight();

    WritableRaster   raster=image.getRaster();
    DataBufferByte   buffer=(DataBufferByte)raster.getDataBuffer();
    byte[]           imgdata=(byte[])buffer.getData();

    BitSwapOutputStream    bsos = new BitSwapOutputStream(baos);
    ModHuffmanOutputStream mhos = new ModModREADOutputStream(bsos,width);
    RLEBit1OutputStream    rlos = new RLEBit1OutputStream(mhos);

    int len=width>>3;                              // eight pixel per byte
    int end=8-(width&0x07);                        // how many bits of last byte represent image data

    int off=0;
    if(end==8){                                    // image row ends at byte boundary
      for(int y=0;y<height;y++){
        rlos.setStartCodeWord(0x0001);             // white run first; White is Zero: 1s in image => 0s in compressed data
        mhos.writeEOL();                           // T.6: we don't write EOL code, we just set up buffers here
        rlos.write(imgdata,off,len);
        rlos.flush();
        off+=len;
      }
    }else{
      for(int y=0;y<height;y++){
        rlos.setStartCodeWord(0x0001);             // white run first; White is Zero: 1s in image => 0s in compressed data
        mhos.writeEOL();                           // T.6: we don't write EOL code, we just set up buffers here
        rlos.write(imgdata,off,len);
        rlos.writeBits(imgdata[off+len],7,end);    // write end of line pixel
        rlos.flush();
        off+=len+1;
      }
    }
    if(mhos instanceof ModModREADOutputStream){    // T.6: write EOFB
      ((ModModREADOutputStream)mhos).writeEOFB();
    }
    rlos.close();
  }

  private void encodeJPEG(ByteArrayOutputStream  baos, BufferedImage image)throws IOException{

    int width  = image.getWidth();
    int height = image.getHeight();

    JFIFOutputStream       os;

    if(image.getType()==BufferedImage.TYPE_BYTE_GRAY){             // one   component;  grey scale
      os =new JFIFOutputStream(baos,false,height,width);           // SOF:start of frame

      WritableRaster   raster=image.getRaster();
      DataBufferByte   buffer=(DataBufferByte)raster.getDataBuffer();
      byte[]           imgdata=(byte[])buffer.getData();

      os.write(imgdata);
      os.close();                                                  // EOF: end of frame
    }else if(image.getType()==BufferedImage.TYPE_INT_RGB){         // three components; YCbCr
      os =new JFIFOutputStream(baos,true,height,width);            // SOF:start of frame

      WritableRaster   raster=image.getRaster();
      DataBufferInt    buffer=(DataBufferInt)raster.getDataBuffer();
      int[]            imgdata=(int[])buffer.getData();

      os.write(imgdata);
      os.close();                                                  // EOF: end of frame
    }      
  }

  public void read(PDFScanner s)throws IOException{
    super.read(s);

    int width  = ((PDFObject.PDFInteger)get("Width")).getValue();
    int height = ((PDFObject.PDFInteger)get("Height")).getValue();

    String[] filters = getFilters();
    int i=0;
    while(i<(filters.length-1)){
//    filter
      i++;
    }
    String filter = filters[i];

    if(filter.equals("DCTDecode")){
      String colorspace = ((PDFObject.PDFName)get("ColorSpace")).getName();
      if(colorspace.equals("DeviceGray")){
//        int bpc    = ((PDFObject.PDFInteger)get("BitsPerComponent")).getValue();
      
        image = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        decodeJPEGGray(image,data);
        return;
      }else if(colorspace.equals("DeviceRGB")){
//        int bpc    = ((PDFObject.PDFInteger)get("BitsPerComponent")).getValue();

        image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        decodeJPEGRGB(image,data);
        return;
      }
      throw new IOException(getClass().getName()+".read:\n\tCannot read image stream. Unknown 'ColorSpace' "+colorspace+".");
    }else if(filter.equals("CCITTFaxDecode")){
      String colorspace = ((PDFObject.PDFName)get("ColorSpace")).getName();
      if(colorspace.equals("DeviceGray")){
        image = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
        decodeT6(image,data);
        return;
      }
    }
    throw new IOException(getClass().getName()+".read:\n\tCannot read image stream. Unknown 'Filter' "+filter+".");
  }

  private void decodeJPEGGray(BufferedImage image,InputStream  data)throws IOException{
    WritableRaster   raster=image.getRaster();
    DataBufferByte   buffer=(DataBufferByte)raster.getDataBuffer();
    byte[]           imgdata=(byte[])buffer.getData();
    new JFIFInputStream(data).read(imgdata);
  }

  private void decodeJPEGRGB(BufferedImage image,InputStream  data)throws IOException{
    WritableRaster   raster=image.getRaster();
    DataBufferInt    buffer=(DataBufferInt)raster.getDataBuffer();
    int[]            imgdata=(int[])buffer.getData();
    new JFIFInputStream(data).read(imgdata);
  }

  private void decodeT6(BufferedImage image,InputStream  data)throws IOException{
    WritableRaster   raster=image.getRaster();
    DataBufferByte   buffer=(DataBufferByte)raster.getDataBuffer();
    byte[]           imgdata=(byte[])buffer.getData();

    int width      = image.getWidth();
//    int height     = image.getHeight();

    BitSwapInputStream    bsis = new BitSwapInputStream(data);
    ModHuffmanInputStream mhis = new ModModREADInputStream(bsis,width);
    RLEBitInputStream     rlis = new RLEBitInputStream(mhis);
//    rlis.setInvert(invert);

    int off=0;
    if((width&0x0007)==0){
      byte[] buf=new byte[width>>3];int len=0;
      while(true){
        rlis.resetToStartCodeWord();                    // start next line with white
        mhis.readEOL();                                 // set settings for a new line
        try{
          len=rlis.read(buf);                           // read one image line
          if(len==-1){break;}                           // end of page
          System.arraycopy(buf,0,imgdata,off,len);      // copy line to image buffer
        }catch(ModHuffmanInputStream.ModHuffmanCodingException mhce){
          System.out.println(getClass().getName()+".copyin:\n\t"+mhce);
        }
        off+=len;
      }
    }else{
      byte[] buf=new byte[(width+7)>>3];int len=0,ecw=8-(width&0x0007),bits;
      while(true){
        rlis.resetToStartCodeWord();                    // start next line with white
        mhis.readEOL();                                 // set settings for a new line
        try{
          len=rlis.read(buf,0,buf.length-1);            // read one image line
          if(len==-1){break;}                           // end of page
          bits=rlis.readBits(7,ecw);
          buf[len]=(byte)bits;
          System.arraycopy(buf,0,imgdata,off,len+1);    // copy line to image buffer
        }catch(ModHuffmanInputStream.ModHuffmanCodingException mhce){
          System.out.println(getClass().getName()+".copyin:\n\t"+mhce);
        }
        off+=len+1;
      }
    } 
  }

}

/*

[1] Portable Document Format Reference Manual
    ISBN 0-201-62628-4
    1996
*/