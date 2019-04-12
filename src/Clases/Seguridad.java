/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author ariel
 */


public class Seguridad {

    
    private byte[] encriptado;
    
    
    public Seguridad() {
        
        encriptado =null;
    
    }

    public void generarLlave(String nombreLlave) throws NoSuchAlgorithmException, FileNotFoundException, IOException {

        // Para tomar los valores para crear una cadena de caracteres que
        // utilizaremos para generar la llave
        String valores = "1234567890abcdefghijklmnopqrstuvwxyz*ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        String textoClave = "";

        //Generando la cadena tomando 16 caracteres al azar
        for (int i = 0; i < 16; i++) {

            //generar números enteros entre 0 y el tamaño de la cadena
            Random r = new Random();
            int pos = r.nextInt(valores.length() - 1);

            textoClave = textoClave + valores.charAt(pos);

        }

        // Generamos una clave de 128 bits adecuada para AES
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        Key llave = keyGenerator.generateKey();

        // Generar una clave que tenga al menos 16 bytes
        // y nos quedamos con los bytes 0 a 15
        llave = new SecretKeySpec(textoClave.getBytes(), 0, 16, "AES");

        //Guardamos la llave en un archivo
        //Solo quien tenga la llave podrá encriptar o desencriptar 
        // la extension del archivo es decisión personal, yo elegí jpk (java private key) jeje
        File archivo = new File(nombreLlave + ".jpk");

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo));

        oos.writeObject(llave);

        oos.close();

    }

    public String EncriptarTexto(File archivoLlave, String texto) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoLlave));

        Object aux = ois.readObject();

        if (aux instanceof Key) {

            Key llave = (Key) aux;

            // Se obtiene un cifrador AES
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Se inicializa para encriptacion y se encripta el texto,
            // que debemos pasar como bytes.
            aes.init(Cipher.ENCRYPT_MODE, llave);
            encriptado = aes.doFinal(texto.getBytes());

            // Mostrando texto encriptado.
            String textoEncriptado = "";

            for (byte b : encriptado) {
                textoEncriptado = textoEncriptado + Integer.toHexString(0xFF & b);
            }

            return textoEncriptado;

        }

        ois.close();

        return null;
    }

    public String desencriptarTexto(File archivoLlave) throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, ClassNotFoundException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoLlave));

        Object aux = ois.readObject();

        if (aux instanceof Key) {

            Key llave = (Key) aux;

            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Se iniciliza el cifrador para desencriptar, con la
            // misma clave y se desencripta
            aes.init(Cipher.DECRYPT_MODE, llave);
            
            
            
            byte[] desencriptado = aes.doFinal(encriptado);

            // Mostrar texto obtenido, igual al original.
            return new String(desencriptado);

          

        }
        
        
          return null;

    }
    
}
