/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import static code.Tokens.Numero;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.JFileChooser;
import java.util.HashMap;


/**
 *
 * @author Charly Ponce
 */
public class frmPantalla extends javax.swing.JFrame {

    /**
     * Creates new form FrmPrincipal
     */
    
    private String erroresSemanticos = "";
    private String ambitoActual = ""; //para almacenar el ambito actual "solo funciona con main"
    HashMap<String, String> variablesDeclaradas = new HashMap<>(); //Para almacenar las variables declaradas
    
    public frmPantalla() {
        initComponents();
        this.setLocationRelativeTo(null);
    }
    
    public void analizarSemantica() throws IOException {
        // Inicialización de variables
        ambitoActual = "";
        erroresSemanticos = "";
        variablesDeclaradas.clear();
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        boolean seEncontraronErrores = false; // Variable para controlar si se encontraron errores
        int linea = 1; // Contador de línea
        String ambitoActual = "otro"; // Ámbito actual (main o otro)

        // Análisis léxico y semántico
        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                break;
            }

            switch (token) {
                case Linea:
                    linea++;
                    break;
                case T_dato:
                    String tipoDato = lexer.lexeme;
                    Tokens nextToken = lexer.yylex();
                    if (nextToken == Tokens.Identificador) {
                        String identificador = lexer.lexeme;
                        // Verificar si la variable se declaro dentro de main o no
                        if (ambitoActual.equals("main")) {
                            //validar si la variable se ha declarado mas de una vez
                            if (variablesDeclaradas.containsKey(identificador)) {
                            erroresSemanticos += " Error: La variable '" + identificador + "' ya ha sido declarada en el mismo ámbito (línea " + linea + ")\n";
                            seEncontraronErrores = true;
                        } else {
                            variablesDeclaradas.put(identificador, tipoDato);
                            // Verificar si hay un valor asignado a la variable y que este sea el corecto
                            Tokens tokenAsignacion = lexer.yylex();
                            if (tokenAsignacion == Tokens.Igual) {
                                Tokens tipoValor = lexer.yylex();
                                if ((tipoDato.equals("int") && tipoValor == Tokens.NumeroDecimal) ||
                                    (tipoDato.equals("float") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal) || 
                                        (tipoDato.equals("double") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal)){
                                    erroresSemanticos += " Error: Tipo de dato incorrecto para la variable '" + identificador + "' (línea " + linea + ")\n";
                                    seEncontraronErrores = true;
                                }
                            }
                        }
                        } else {
                            erroresSemanticos += " Error: Variable '" + identificador + "' declarada fuera de 'main' (línea " + linea + ")\n";
                            seEncontraronErrores = true;
                        }
                    }
                    break;
                case Identificador:
                    String identificador = lexer.lexeme;
                    // Verificar si la variable no ha sido declarada
                    if (!variablesDeclaradas.containsKey(identificador)) {
                        erroresSemanticos += " Error: Variable no declarada '" + identificador + "' (línea " + linea + ")\n";
                        seEncontraronErrores = true;
                    } else {
                        Tokens tokenAsignacion = lexer.yylex();
                        // Verificar si hay un valor asignado a la variable y que este sea el corecto
                        if (tokenAsignacion == Tokens.Igual) {
                            String tipoDatoVariable = variablesDeclaradas.get(identificador);
                            Tokens tipoValor = lexer.yylex();
                            if ((tipoDatoVariable.equals("int") && tipoValor == Tokens.NumeroDecimal) ||
                                (tipoDatoVariable.equals("float") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal) ||
                                (tipoDatoVariable.equals("double") && tipoValor != Tokens.Numero && tipoValor != Tokens.NumeroDecimal)) {
                                erroresSemanticos += " Error: Tipo de dato incorrecto para la variable '" + identificador + "' (línea " + linea + ")\n";
                                seEncontraronErrores = true;
                            }
                        }
                    }
                    break;
                case Llave_a:
                    ambitoActual = "main";
                    break;
                case Llave_c:
                    ambitoActual = "otro";
                    break;
                default:
                    break;
            }
        }

        // Verificación final de errores y mensajes
         if (!seEncontraronErrores) {
            erroresSemanticos += "No hay errores semánticos. \n";
        }

         // Mostrar los errores semánticos
        txtAnalizarSem.setText(erroresSemanticos);
    }

    
    private void analizarSintactico(){
        // TODO add your handling code here:
        String ST = txtResultado.getText();
        Sintax s = new Sintax(new code.LexerCup(new StringReader(ST)));
        
        try {
            s.parse();
            txtAnalizarSin.setText("Analisis realizado correctamente");
            txtAnalizarSin.setForeground(new Color(25, 111, 61));
        } catch (Exception ex) {
            Symbol sym = s.getS();
            txtAnalizarSin.setText("Error de sintaxis. Linea: " + (sym.right + 1) + " Columna: " + (sym.left + 1) + ", Texto: \"" + sym.value + "\"");
            txtAnalizarSin.setForeground(Color.red);
        }
    }
    
    private void analizarLexico() throws IOException{
        int cont = 1;
        
        String expr = (String) txtResultado.getText();
        Lexer lexer = new Lexer(new StringReader(expr));
        String resultado = "Linea " + cont + "\t\tToken\n";
        while (true) {
            Tokens token = lexer.yylex();
            if (token == null) {
                txtAnalizarLex.setText(resultado);
                return;
            }
            switch (token) {
                case Linea:
                    cont++;
                    resultado += "Linea " + cont + "\n";
                    break;
                case Comillas:
                    resultado += "  Comillas\t\t" + lexer.lexeme + "\n";
                    break;
                case Cadena:
                    resultado += "  Tipo de dato\t" + lexer.lexeme + "\n";
                    break;
                case T_dato:
                    resultado += "  Tipo de dato\t\t" + lexer.lexeme + "\n";
                    break;
                case If:
                    resultado += "  Reservada if\t" + lexer.lexeme + "\n";
                    break;
                case Else:
                    resultado += "  Reservada else\t" + lexer.lexeme + "\n";
                    break;
                case Do:
                    resultado += "  Reservada do\t" + lexer.lexeme + "\n";
                    break;
                case While:
                    resultado += "  Reservada while\t" + lexer.lexeme + "\n";
                    break;
                case For:
                    resultado += "  Reservada while\t" + lexer.lexeme + "\n";
                    break;
                case Igual:
                    resultado += "  Operador igual\t" + lexer.lexeme + "\n";
                    break;
                case Suma:
                    resultado += "  Operador suma\t" + lexer.lexeme + "\n";
                    break;
                case Resta:
                    resultado += "  Operador resta\t" + lexer.lexeme + "\n";
                    break;
                case Multiplicacion:
                    resultado += "  Operador multiplicacion\t" + lexer.lexeme + "\n";
                    break;
                case Division:
                    resultado += "  Operador division\t" + lexer.lexeme + "\n";
                    break;
                case Op_logico:
                    resultado += "  Operador logico\t" + lexer.lexeme + "\n";
                    break;
                case Op_incremento:
                    resultado += "  Operador incremento\t" + lexer.lexeme + "\n";
                    break;
                case Op_relacional:
                    resultado += "  Operador relacional\t" + lexer.lexeme + "\n";
                    break;
                case Op_atribucion:
                    resultado += "  Operador atribucion\t" + lexer.lexeme + "\n";
                    break;
                case Op_booleano:
                    resultado += "  Operador booleano\t" + lexer.lexeme + "\n";
                    break;
                case Parentesis_a:
                    resultado += "  Parentesis de apertura\t" + lexer.lexeme + "\n";
                    break;
                case Parentesis_c:
                    resultado += "  Parentesis de cierre\t" + lexer.lexeme + "\n";
                    break;
                case Llave_a:
                    resultado += "  Llave de apertura\t" + lexer.lexeme + "\n";
                    break;
                case Llave_c:
                    resultado += "  Llave de cierre\t\t" + lexer.lexeme + "\n";
                    break;
                case Corchete_a:
                    resultado += "  Corchete de apertura\t" + lexer.lexeme + "\n";
                    break;
                case Corchete_c:
                    resultado += "  Corchete de cierre\t" + lexer.lexeme + "\n";
                    break;
                case Main:
                    resultado += "  Reservada main\t" + lexer.lexeme + "\n";
                    break;
                case P_coma:
                    resultado += "  Punto y coma\t\t" + lexer.lexeme + "\n";
                    break;
                case Identificador:
                    resultado += "  Identificador\t\t" + lexer.lexeme + "\n";
                    break;
                case Numero:
                    resultado += "  Numero\t\t" + lexer.lexeme + "\n";
                    break;
                case NumeroDecimal:
                    resultado += "  NumeroDecimal\t" + lexer.lexeme + "\n";
                    break;
                case ERROR:
                    resultado += "  Simbolo no definido\n";
                    break;
                default:
                    resultado += "   " + lexer.lexeme + " \n";
                    break;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAnalizarLex = new javax.swing.JTextArea();
        btnAnalizarLex = new javax.swing.JButton();
        btnLimpiarLex = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAnalizarSin = new javax.swing.JTextArea();
        btnAnalizarSin = new javax.swing.JButton();
        btnLimpiarSin = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        btnAnalisis = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnLimpiarEditor = new javax.swing.JButton();
        btnLimpiarGeneral = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtAnalizarSem = new javax.swing.JTextArea();
        btnAnalizarSem = new javax.swing.JButton();
        btnLimpiarSem = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Semantic Analyzer");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(204, 255, 255));

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lexical Analysis", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 36))); // NOI18N

        txtAnalizarLex.setEditable(false);
        txtAnalizarLex.setBackground(new java.awt.Color(204, 204, 204));
        txtAnalizarLex.setColumns(20);
        txtAnalizarLex.setFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        txtAnalizarLex.setRows(5);
        jScrollPane2.setViewportView(txtAnalizarLex);

        btnAnalizarLex.setBackground(new java.awt.Color(102, 255, 102));
        btnAnalizarLex.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnAnalizarLex.setText("EJECUTAR");
        btnAnalizarLex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarLexActionPerformed(evt);
            }
        });

        btnLimpiarLex.setBackground(new java.awt.Color(255, 255, 0));
        btnLimpiarLex.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnLimpiarLex.setText("BORRAR");
        btnLimpiarLex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarLexActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnAnalizarLex)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                        .addComponent(btnLimpiarLex)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiarLex)
                    .addComponent(btnAnalizarLex))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Syntactic analysis", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 36))); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(475, 242));

        txtAnalizarSin.setEditable(false);
        txtAnalizarSin.setBackground(new java.awt.Color(204, 204, 204));
        txtAnalizarSin.setColumns(20);
        txtAnalizarSin.setFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        txtAnalizarSin.setRows(5);
        jScrollPane3.setViewportView(txtAnalizarSin);

        btnAnalizarSin.setBackground(new java.awt.Color(102, 255, 102));
        btnAnalizarSin.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnAnalizarSin.setText("EJECUTAR");
        btnAnalizarSin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarSinActionPerformed(evt);
            }
        });

        btnLimpiarSin.setBackground(new java.awt.Color(255, 255, 0));
        btnLimpiarSin.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnLimpiarSin.setText("BORRAR");
        btnLimpiarSin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarSinActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAnalizarSin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                        .addComponent(btnLimpiarSin)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAnalizarSin)
                    .addComponent(btnLimpiarSin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "REDACTOR DE CÓDIGOS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 36))); // NOI18N

        txtResultado.setBackground(new java.awt.Color(204, 204, 204));
        txtResultado.setColumns(20);
        txtResultado.setFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        txtResultado.setRows(5);
        jScrollPane1.setViewportView(txtResultado);

        jPanel4.setBackground(new java.awt.Color(204, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "General", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        btnAnalisis.setBackground(new java.awt.Color(102, 255, 102));
        btnAnalisis.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnAnalisis.setText("EJECUTAR");
        btnAnalisis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalisisActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 0, 51));
        jButton1.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        jButton1.setText("CERRAR");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnLimpiarEditor.setBackground(new java.awt.Color(255, 255, 0));
        btnLimpiarEditor.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnLimpiarEditor.setText("BORRAR");
        btnLimpiarEditor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarEditorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAnalisis, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(btnLimpiarEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btnAnalisis)
                .addGap(33, 33, 33)
                .addComponent(btnLimpiarEditor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );

        btnLimpiarGeneral.setBackground(new java.awt.Color(255, 255, 0));
        btnLimpiarGeneral.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnLimpiarGeneral.setText("BORRAR TODO");
        btnLimpiarGeneral.setToolTipText("");
        btnLimpiarGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarGeneralActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLimpiarGeneral)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnLimpiarGeneral)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(204, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Semantic Analysis", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 36))); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(475, 242));

        txtAnalizarSem.setEditable(false);
        txtAnalizarSem.setBackground(new java.awt.Color(204, 204, 204));
        txtAnalizarSem.setColumns(20);
        txtAnalizarSem.setFont(new java.awt.Font("Agency FB", 0, 36)); // NOI18N
        txtAnalizarSem.setRows(5);
        jScrollPane4.setViewportView(txtAnalizarSem);

        btnAnalizarSem.setBackground(new java.awt.Color(102, 255, 102));
        btnAnalizarSem.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnAnalizarSem.setText("EJECUTAR");
        btnAnalizarSem.setName(""); // NOI18N
        btnAnalizarSem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarSemActionPerformed(evt);
            }
        });

        btnLimpiarSem.setBackground(new java.awt.Color(255, 255, 0));
        btnLimpiarSem.setFont(new java.awt.Font("Agency FB", 1, 36)); // NOI18N
        btnLimpiarSem.setText("BORRAR");
        btnLimpiarSem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarSemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnAnalizarSem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                        .addComponent(btnLimpiarSem)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAnalizarSem)
                    .addComponent(btnLimpiarSem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.getAccessibleContext().setAccessibleName("REDACTOR DE CODIGO");
        jPanel3.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimpiarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarLexActionPerformed
        // TODO add your handling code here:
        txtAnalizarLex.setText(null);
    }//GEN-LAST:event_btnLimpiarLexActionPerformed

    private void btnLimpiarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarSinActionPerformed
        // TODO add your handling code here:
        txtAnalizarSin.setText(null);
    }//GEN-LAST:event_btnLimpiarSinActionPerformed

    private void btnAnalizarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarLexActionPerformed
        try {
            analizarLexico();
        } catch (IOException ex) {
            Logger.getLogger(frmPantalla.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalizarLexActionPerformed

    private void btnAnalizarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarSinActionPerformed
        analizarSintactico();
    }//GEN-LAST:event_btnAnalizarSinActionPerformed

    private void btnLimpiarEditorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarEditorActionPerformed
        // TODO add your handling code here:
        txtResultado.setText(null);
    }//GEN-LAST:event_btnLimpiarEditorActionPerformed

    private void btnAnalisisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalisisActionPerformed
        // TODO add your handling code here:
        try {
            analizarLexico();
            analizarSintactico();
            analizarSemantica();
        } catch (IOException ex) {
            Logger.getLogger(frmPantalla.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalisisActionPerformed

    private void btnLimpiarGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarGeneralActionPerformed
        // TODO add your handling code here:
        txtResultado.setText(null);
        txtAnalizarLex.setText(null);
        txtAnalizarSin.setText(null);
        txtAnalizarSem.setText(null);
    }//GEN-LAST:event_btnLimpiarGeneralActionPerformed

    private void btnAnalizarSemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarSemActionPerformed
        try {
            // TODO add your handling code here:
            analizarSemantica();
        } catch (IOException ex) {
            Logger.getLogger(frmPantalla.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalizarSemActionPerformed

    private void btnLimpiarSemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarSemActionPerformed
        // TODO add your handling code here:
        txtAnalizarSem.setText(null);
    }//GEN-LAST:event_btnLimpiarSemActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmPantalla.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmPantalla.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmPantalla.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmPantalla.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmPantalla().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnalisis;
    private javax.swing.JButton btnAnalizarLex;
    private javax.swing.JButton btnAnalizarSem;
    private javax.swing.JButton btnAnalizarSin;
    private javax.swing.JButton btnLimpiarEditor;
    private javax.swing.JButton btnLimpiarGeneral;
    private javax.swing.JButton btnLimpiarLex;
    private javax.swing.JButton btnLimpiarSem;
    private javax.swing.JButton btnLimpiarSin;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea txtAnalizarLex;
    private javax.swing.JTextArea txtAnalizarSem;
    private javax.swing.JTextArea txtAnalizarSin;
    private javax.swing.JTextArea txtResultado;
    // End of variables declaration//GEN-END:variables
}
