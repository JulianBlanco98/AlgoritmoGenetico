package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import Datos.Aula;
import Datos.Horario;
import Evaluar.IndividualH;
import Evaluar.PoblacionH;
import Fichero.GestionFichero;
/**
 * Clase que contiene el main para lanzar el algoritmo gen閠ico. Contiene todos los par醡etros del algoritmo gen閠ico
 * que est醤 en el archivo "Config.txt".
 * En esta clase se usan los 3 paquetes del proyecto:
 * <ul>
 * 		<li><strong>Paquete Datos</strong>: Contiene las clases necesarias para crear el individuo horario (genotipo y fenotipo)</li>
 * 		<li><strong>Paquete Evaluar</strong>: Contiene los m閠odos de evaluaci髇 de la fitness y los operadores gen閠icos (selecci髇, cruce y mutaci髇)</li>
 * 		<li><strong>Paquete Fichero</strong>: Contiene la clase para leer el archivo "Config.txt"</li>
 * </ul>
 * @author julian
 *
 */
public class mainHorario {
	
	public static void main(String[] args) {
		
		
		//Variables-----------------------
		//Definicion del problema
		int numeroCursos= 0;
		int numeroAsignaturas = 0;
		int numeroHorasAsignatura = 0;
		int aulas = 0;
		int dias = 0;
		int periodoDia = 0;
		
		//Datos para el algoritmo
		int poblacion = 0;
		int numGeneraciones = 0;
		float probCruce = 0f;
		float probMutacion = 0f;
		int elitismo = 0;
		int estrategiaRemplazamiento = 0;
		int operadorCruce = 0;
		int operadorMutacion = 0;
		
		//Selector de algoritmo
		int selectorAlgoritmo= 0;
		
		
		//Instancias
		GestionFichero gestionF = new GestionFichero();
		
		//Recuperar los datos del fichero Config.txt
		ArrayList<Double> datos = new ArrayList<>();
		String archivoConfig = "src/main/Config.txt";
		
		System.out.println("PROGRAMA PARA GENERAR UN HORARIO");
		System.out.println("Primero: leer las variables del fichero Config.txt");
		datos = gestionF.leerFicheroConfig(archivoConfig);
		int indice = 0;
		Iterator<Double> it = datos.iterator();
		while (it.hasNext()) {
			double valor = it.next();
			switch (indice) {
			case 0: poblacion = (int) valor; break; case 1: numGeneraciones = (int) valor; break; case 2: probCruce = (float) valor; break; case 3: probMutacion = (float) valor; break; case 4: elitismo = (int) valor; break;
			case 5: estrategiaRemplazamiento = (int) valor; break; case 6: operadorCruce = (int) valor; break; case 7: operadorMutacion = (int) valor; break; case 8: selectorAlgoritmo = (int) valor; break; case 9: numeroCursos = (int) valor; break;
			case 10: numeroAsignaturas = (int) valor; break; case 11: numeroHorasAsignatura = (int) valor; break; case 12: aulas = (int) valor; break; case 13: dias = (int) valor; break; case 14: periodoDia = (int) valor; break; default: break;
			}
			indice++; // Incrementar el 韓dice para acceder al siguiente valor
		}
		
		poblacion = poblacion + elitismo; //Sumar el elitismo al tama駉 de la poblaci髇
		
		//Crear la poblaci髇 inicial con el constructor parametrizado
		PoblacionH pop = new PoblacionH(numeroCursos, numeroAsignaturas, numeroHorasAsignatura, aulas, dias, periodoDia,
		        poblacion, numGeneraciones, probCruce, probMutacion, elitismo, estrategiaRemplazamiento,
		        operadorCruce, operadorMutacion, selectorAlgoritmo);
		
		//Crear la siguiente poblaci髇 a la actual
		IndividualH[] newPoblacion = new IndividualH[poblacion]; 
		
		//Descendientes
		IndividualH[] individuo = new IndividualH[2];
		
		//Padres: estacionario
		IndividualH[] padres = new IndividualH[2];
				
		// Poblaci髇 actual
		System.out.println("Iteration: 0");
		System.out.print("Total Fitness = " + pop.getTotalFitness());
		//Luego se escoge el mejor individuo (horario) de acuerdo a su fitness. Y se muestra por pantalla
		System.out.print(" ; Best Fitness = " + pop.findBestIndividual().getFitnessValue());
		System.out.println("; Media Fitness = "+pop.calcularMediaFitness());
		System.out.print("Best Individual: ");
		pop.findBestIndividual().getIndividuo().mostrarAulasConAsignaturas(periodoDia, (dias * aulas), aulas);
		
			
		int contador;
		int iteracion = 0;
		for(iteracion=0; iteracion < numGeneraciones; iteracion++) {
			contador = 0;
			
			
			//Elitismo. Escoger los x mejores individuos y pasarlos a la nueva poblacion
			
			if(elitismo > 0) {
				//Conseguir los mejores individuos de la poblacion
				IndividualH[] individuosElitistas;
				individuosElitistas = pop.getTopElites();
				//A馻do los individuos a la nueva poblacion
				for(int i=0;i<elitismo;i++) {
					newPoblacion[contador] = individuosElitistas[i];
					contador++;
				}
			}
			
			//Construir nueva poblaci髇
			while (contador < poblacion) {

				// Algoritmos de selecci髇 (4)
				switch (selectorAlgoritmo) {
				case 0: // Ruleta
					individuo[0] = pop.selectorPorRuleta();
					individuo[1] = pop.selectorPorRuleta();
					break;
				case 1: // Ranking
					individuo[0] = pop.selectorPorRanking();
					individuo[1] = pop.selectorPorRanking();
					break;
				case 2: // Torneo
					individuo[0] = pop.selectorPorTorneo();
					individuo[1] = pop.selectorPorTorneo();
					break;
				case 3: // Truncada: par醡etro va a ser el 15% de la poblacion
					int horariosMalos = (int) Math.round(poblacion * 0.15);
					individuo[0] = pop.selectorTruncada((poblacion - horariosMalos));
					individuo[1] = pop.selectorTruncada((poblacion - horariosMalos));
					break;
				default:
					throw new IllegalArgumentException("Algoritmo de selecci髇 inv醠ido");
				}
				
				//Si la estrategia de reemplzamiento es estacionario, guardo los 2 padres
				if(estrategiaRemplazamiento == 1) {
					padres[0] = individuo[0];
					padres[1] = individuo[1];
				}

				// Crossover
				/*
				 * Aplicamos probabilidad de cruce, para ver si hay cruce. En caso de haber
				 * cruze, los padres ser谩n sustituidos por los hijos En caso contrario, los
				 * padres sobreviven.
				 */
				if (pop.getm_rand() < probCruce) {
					/// individuo = pop;
					/*
					 * System.out.println("\nPADRE 1---------------------------");
					 * individuo[0].mostrarHorario_Individuo(periodoDia, (dias*aulas), aulas);
					 * System.out.println("\nPADRE 2---------------------------");
					 * individuo[1].mostrarHorario_Individuo(periodoDia, (dias*aulas), aulas);
					 */

					individuo = pop.cruzarHorarios(individuo[0], individuo[1], operadorCruce, dias);

					/*
					 * System.out.println("\nDESPU蒘 DEL CRUCE");
					 * System.out.println("Descendiente 1---------------------------");
					 * individuo[0].mostrarHorario_Individuo(periodoDia, (dias*aulas), aulas);
					 * System.out.println("Descendiente 2---------------------------");
					 * individuo[1].mostrarHorario_Individuo(periodoDia, (dias*aulas), aulas);
					 */

					// Mutation
					/*
					 * Aplicamos la probabilidad de mutaci贸n, para ver si se da mutaci贸n en los
					 * individuos Estos individuos ser谩n los que se han generado al producir el
					 * cruce (hijos) o no (padres) Si se da que hay mutci贸n, la mutaci贸n
					 * coedificada el flip (cambio de bit) en uno de los bits de forma aleatoria
					 */
					if (pop.getm_rand() < probMutacion) {
						
						
						System.out.println("\nDESCENDIENTE 1 SIN MUTACION HECHA");
						individuo[0].mostrarHorario_Individuo(periodoDia, (dias*aulas), aulas);
						individuo[0].mutacion(operadorMutacion, dias, aulas);
						System.out.println("\nDESCENDIENTE 1 CON MUTACION HECHA");
						individuo[0].mostrarHorario_Individuo(periodoDia, (dias*aulas), aulas);
					}
					if (pop.getm_rand() < probMutacion) {
						individuo[1].mutacion(operadorMutacion, dias, aulas);
					}

				}
				switch(estrategiaRemplazamiento) {
				//Generacional: coger los 2 hijos siempre para generar la neuva poblaci髇
				case 0: 
					newPoblacion[contador] = individuo[0];
					newPoblacion[contador + 1] = individuo[1];	
					contador += 2;
					break;
				//Estacionario: coger entre los 2 padres y los 2 hijos, los 2 que mejor fitness tengan	
				case 1:					
					IndividualH[] mejores2 = new IndividualH[4];
					mejores2[0] = padres[0];
					mejores2[1] = padres[1];
					mejores2[2] = individuo[0];
					mejores2[3] = individuo[1];
					
					//Ordenar seg鷑 la fitness
					Arrays.sort(mejores2, Comparator.comparingDouble(IndividualH::getFitnessValue).reversed());
										
					//A馻dir los 2 primeros (mejor fitness)
					newPoblacion[contador] = mejores2[0];
					newPoblacion[contador+1] = mejores2[1];
										
					contador +=2;
					break;
				default:
					throw new IllegalArgumentException("Estrategia de Reemplazamiento incorrecta");
				}

				

			}
			
			//Crear la nueva poblaci髇 con estacionario o generacional y mostrar sus valores
			
			pop.setPopulation(newPoblacion);
			pop.evaluate(numeroCursos, numeroAsignaturas, numeroHorasAsignatura);
			System.out.println("\nIteration: " + iteracion);
			System.out.print("Total Fitness = " + pop.getTotalFitness());
			System.out.print(" ; Best Fitness = " + pop.findBestIndividual().getFitnessValue());
			System.out.println("; Media Fitness = "+pop.calcularMediaFitness());
			//System.out.print("Best Individual: ");
			//pop.findBestIndividual().getIndividuo().mostrarAulasConAsignaturas(periodoDia, (dias*aulas), aulas);;
			
		}
		System.out.println("\nMEJOR INDIVIDUO----------------------------------");
		IndividualH mejorHorario = pop.findBestIndividual();
		System.out.print("Total Fitness = " + pop.getTotalFitness());
		System.out.println(" ; Best Fitness = " + mejorHorario.getFitnessValue());
		mejorHorario.getIndividuo().mostrarAulasConAsignaturas(periodoDia, (dias*aulas), aulas);
		
		mejorHorario.evaluarFitness(aulas, numeroCursos, numeroAsignaturas, numeroHorasAsignatura);
		
		//Verifica que el horario resultado tiene todas las clases seg鷑 los par醡etros introducidos
		int total = mejorHorario.contarTotalClases();
		System.out.println("\nTotal clases: "+total);
		
		
		System.out.println("\nFinal del programa");
		
		
	}
	
	
}
