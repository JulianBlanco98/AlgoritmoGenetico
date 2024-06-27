package Fichero;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 * Clase para leer el fichero Config.txt
 * @author julian
 *
 */
public class GestionFichero {

	/**
	 * Método que lee el fichero y devuelve el un arraylist de formato double, los datos del archivo Config.txt
	 * @param dirArchivo	Dirección del archivo
	 * @return arraylist lleno con los parámetros del archivo
	 */
	public ArrayList<Double> leerFicheroConfig(String dirArchivo) {
		
		ArrayList <Double>datos = new ArrayList<>();
		FileReader fr = null;

		try {
			fr = new FileReader(dirArchivo);
			BufferedReader entrada = new BufferedReader(fr);
			String cadena;
			
			while ((cadena = entrada.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(cadena, ":"); 
                String ultimoToken = null;
                while (tokenizer.hasMoreTokens()) {
                    ultimoToken = tokenizer.nextToken().trim(); 
                }
                if (ultimoToken != null) {
                    try {
                        double valor = Double.parseDouble(ultimoToken);
                        datos.add(valor);
                    } catch (NumberFormatException e) {
                        // Si no se puede convertir a double, omitir esta línea
                        System.err.println("No se pudo convertir a double: " + ultimoToken);
                    }
                }
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return datos;
	}
	
}
