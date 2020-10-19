package pers.gongdaowen.ofd;

import pers.gongdaowen.ofd.model.*;
import pers.gongdaowen.ofd.utils.*;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * Reference：http://c.gb688.cn/bzgk/gb/showGb?type=online&hcno=3AF6682D939116B6F5EED53D01A9DB5D
 */
public class OfdParser {

    public static OFDInfo parse(File file) throws Exception {
        // 读取文件
        try (OFDFile zipFile = new OFDFile(file)) {
            // 1. 读取OFD.xml文件
            OFD ofd = OfdUtils.xmlToObject(zipFile.getStream("OFD.xml"), OFD.class);
            if (BeanUtils.isEmpty(ofd)) {
                throw new RuntimeException("文件格式不支持");
            }
            if (BeanUtils.isEmpty(ofd.DocBody) || BeanUtils.isEmpty(ofd.DocBody.DocRoot)) {
                throw new RuntimeException("文件结构不符合规范");
            }

            // 2. 读取Document.xml文件
            OFDDocument ofdDoc = ofd.DocBody.$OFDDocument = OfdUtils.xmlToObject(zipFile.getStream(ofd.DocBody.DocRoot), OFDDocument.class);
            if (BeanUtils.isNotEmpty(ofdDoc.CommonData)) {
                // 公共资源
                String pubRes = ofdDoc.CommonData.PublicRes;
                if (BeanUtils.isNotEmpty(pubRes)) {
                    OFDDocumentRes ofdRes = ofdDoc.CommonData.$OFDDocumentRes = OfdUtils.xmlToObject(zipFile.getStream(pubRes), OFDDocumentRes.class);
                    // 图片等信息读取字节数组
                    if (BeanUtils.isNotEmpty(ofdRes.MultiMedias)) {
                        for (OFDDocumentRes.MultiMedia media : ofdRes.MultiMedias.MultiMedia) {
                            String baseLoc = "";
                            if (BeanUtils.isNotEmpty(ofdRes.BaseLoc)) {
                                baseLoc = ofdRes.BaseLoc + "/";
                            }
                            media.$FileData = zipFile.getBytes(baseLoc + media.MediaFile);
                            ofd.ObjectMap.put(media.ID, media);
                        }
                    }
                    // 颜色
                    if (BeanUtils.isNotEmpty(ofdRes.ColorSpaces)) {
                        for (OFDDocumentRes.ColorSpace color : ofdRes.ColorSpaces.ColorSpace) {
                            ofd.ObjectMap.put(color.ID, color);
                        }
                    }
                    // 字体
                    if (BeanUtils.isNotEmpty(ofdRes.Fonts)) {
                        for (OFDDocumentRes.Font font : ofdRes.Fonts.Font) {
                            ofd.ObjectMap.put(font.ID, font);
                        }
                    }
                    // 画布参数
                    if (BeanUtils.isNotEmpty(ofdRes.DrawParams)) {
                        for (OFDDocumentRes.DrawParam drawParam : ofdRes.DrawParams.DrawParam) {
                            ofd.ObjectMap.put(drawParam.ID, drawParam);
                        }
                    }
                }
                //页面资源
                String docRes = ofdDoc.CommonData.DocumentRes;
                if (BeanUtils.isNotEmpty(docRes)) {
                    OFDDocumentRes ofdRes = ofdDoc.CommonData.$OFDDocumentRes = OfdUtils.xmlToObject(zipFile.getStream(docRes), OFDDocumentRes.class);
                    // 图片等信息读取字节数组
                    if (BeanUtils.isNotEmpty(ofdRes.MultiMedias)) {
                        for (OFDDocumentRes.MultiMedia media : ofdRes.MultiMedias.MultiMedia) {
                            String baseLoc = "";
                            if (BeanUtils.isNotEmpty(ofdRes.BaseLoc)) {
                                baseLoc = ofdRes.BaseLoc + "/";
                            }
                            media.$FileData = zipFile.getBytes(baseLoc + media.MediaFile);
                            ofd.ObjectMap.put(media.ID, media);
                        }
                    }
                    // 颜色
                    if (BeanUtils.isNotEmpty(ofdRes.ColorSpaces)) {
                        for (OFDDocumentRes.ColorSpace color : ofdRes.ColorSpaces.ColorSpace) {
                            ofd.ObjectMap.put(color.ID, color);
                        }
                    }
                    // 字体
                    if (BeanUtils.isNotEmpty(ofdRes.Fonts)) {
                        for (OFDDocumentRes.Font font : ofdRes.Fonts.Font) {
                            ofd.ObjectMap.put(font.ID, font);
                        }
                    }
                    // 画布参数
                    if (BeanUtils.isNotEmpty(ofdRes.DrawParams)) {
                        for (OFDDocumentRes.DrawParam drawParam : ofdRes.DrawParams.DrawParam) {
                            ofd.ObjectMap.put(drawParam.ID, drawParam);
                        }
                    }
                }
                // TemplatePage
                if (BeanUtils.isNotEmpty(ofdDoc.CommonData.TemplatePage)) {
                    for (OFDDocument.Page page : ofdDoc.CommonData.TemplatePage) {
                        page.$OFDContent = OfdUtils.xmlToObject(zipFile.getStream(page.BaseLoc), OFDContent.class);
                        ofd.ObjectMap.put(page.ID, page);
                    }
                }
            }
            // Page
            if (BeanUtils.isNotEmpty(ofdDoc.Pages)) {
                for (OFDDocument.Page page : ofdDoc.Pages.Page) {
                    page.$OFDContent = OfdUtils.xmlToObject(zipFile.getStream(page.BaseLoc), OFDContent.class);
                    ofd.ObjectMap.put(page.ID, page);
                }
            }
            // Annotations
            if (BeanUtils.isNotEmpty(ofdDoc.Annotations)) {
                ofdDoc.$OFDAnnotations = OfdUtils.xmlToObject(zipFile.getStream(ofdDoc.Annotations), OFDAnnotations.class);
            }
            // Attachments
            if (BeanUtils.isNotEmpty(ofdDoc.Attachments)) {
                ofdDoc.$OFDAttachments = OfdUtils.xmlToObject(zipFile.getStream(ofdDoc.Attachments), OFDAttachments.class);
                for (OFDAttachments.Attachment attach : ofdDoc.$OFDAttachments.Attachment) {
                    attach.$FileData = zipFile.getBytes("Attachs/" + attach.FileLoc);
                    ofd.ObjectMap.put(attach.ID, attach);
                }
            }
            // 3. 读取Signatures.xml文件
            if (BeanUtils.isNotEmpty(ofd.DocBody.Signatures)) {
                OFDSignatures ofdSigns = ofd.DocBody.$OFDSignatures = OfdUtils.xmlToObject(zipFile.getStream(ofd.DocBody.Signatures), OFDSignatures.class);
                for (OFDSignatures.Sign sign : ofdSigns.Signature) {
                    // 读取实际的签名文件
                    OFDSignatures.Signature signature = sign.$Signature = OfdUtils.xmlToObject(zipFile.getStream(sign.BaseLoc), OFDSignatures.Signature.class);

                    // 读取签名信息（数据，签名，证书）
                    ASN1Sequence datSequence = (ASN1Sequence) new ASN1InputStream(zipFile.getStream(signature.SignedValue)).readObject();
                    signature.$ToSignData = ((ASN1Sequence) datSequence.getObjectAt(0)).getEncoded();
                    signature.$SignData   = ((DERBitString) datSequence.getObjectAt(3)).getBytes();
                    signature.$CertData   = ((DEROctetString) datSequence.getObjectAt(1)).getOctets();
                    // 签名文件内容
                    signature.$FileData = zipFile.getBytes(signature.SignedValue);

                    if (BeanUtils.isNotEmpty(signature.SignedInfo)) {
                        // 信息文件签名信息
                        if (BeanUtils.isNotEmpty(signature.SignedInfo.References)) {
                            for (OFDSignatures.Reference reference : signature.SignedInfo.References.Reference) {
                                reference.$FileData = zipFile.getBytes(reference.FileRef);
                            }
                        }
                        // 签章信息
                        OFDSignatures.Seal seal = signature.SignedInfo.Seal;
                        if(BeanUtils.isEmpty(seal) && BeanUtils.isNotEmpty(signature.SignedValue)) {
                        	seal = signature.SignedInfo.Seal = new OFDSignatures.Seal();
                        	seal.BaseLoc = signature.SignedValue;
                        }
                        if (BeanUtils.isNotEmpty(seal)) {
                            seal.$FileData = zipFile.getBytes(seal.BaseLoc);
                            File tmpFile = File.createTempFile("ofd-", ".esl");
                            try (FileOutputStream os = new FileOutputStream(tmpFile)) {
                                os.write(seal.$FileData);
                                os.flush();

                                // 签章文件内容
                                seal.$OFD = parse(tmpFile).getOfd();
                            }
                        }
                    }
                    ofd.ObjectMap.put("SIGN_" + sign.ID, sign);
                }
            }
            return new OFDInfo(ofd);
        }
    }

    public static class OFDFile extends ZipFile {

        public OFDFile(File file) throws IOException {
            super(file);
        }

        public InputStream getStream(String path) throws IOException {
            if (path == null || "".equals(path)) {
                return null;
            }
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (!path.startsWith("Doc_0") && !path.equalsIgnoreCase("OFD.xml")) {
                path = "Doc_0/" + path;
            }
            return this.getInputStream(this.getEntry(path));
        }

        public byte[] getBytes(String path) throws IOException {
            try (InputStream is = this.getStream(path)) {
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                return bytes;
            }
        }

    }
}
