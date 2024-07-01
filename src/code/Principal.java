/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package code;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author STW
 */
public class Principal {
    public static void main(String[] args) throws Exception {
        String ruta1 = "C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/src/code/Lexer.flex";
        String ruta2 = "C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/src/code/LexerCup.flex";
        String[] rutaS = {"-parser", "Sintax", "C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/src/code/Sintax.cup"};
        generate(ruta1, ruta2, rutaS);
    }
    public static void generate(String ruta1, String ruta2, String[] rutaS) throws IOException, Exception{
        File archivo; 
        archivo = new File(ruta1);
        JFlex.Main.generate(archivo);
        archivo = new File(ruta2);
        JFlex.Main.generate(archivo);
        java_cup.Main.main(rutaS);
        
        
        Path rutaSym = Paths.get("C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/src/code/sym.java");
        if (Files.exists(rutaSym)){
              Files.delete(rutaSym);
        }
        Files.move(
                Paths.get("C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/sym.java"),
                Paths.get("C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/src/code/sym.java")
        );
        Path rutaSin = Paths.get("C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/src/code/Sintax.java");
        if (Files.exists(rutaSin)){
              Files.delete(rutaSin);
        }
        Files.move(
                Paths.get("C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/Sintax.java"),
                Paths.get("C:/Users/STW/Documents/NetBeansProjects/AnalizadorSemantico/src/code/Sintax.java")
        );
    }
}
