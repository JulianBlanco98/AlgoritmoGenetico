package Datos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * Clase más importante del paquete datos que sirve para representar el genotipo de un horario. Tiene como parámetro:
 * - Aula [][] hoario: matriz del objeto aula. Filas: período, columnas: días * numeroAulas
 * @author julian
 *
 */
public class Horario {
	
	private Aula [][] horario; //matriz de horas por dias rellenadas de aulas
		
	public Horario() {}
		
	public Horario(Aula [][] horario) {
		super();
		this.horario = horario;		
	}
	/**
	 * Método que genera un horario de manera aleatoria con los parámetros del archivo "Config.txt"
	 * @param dias         		Número de días del horario.
	 * @param periodo      		Número de horas por día en el horario.
	 * @param numCursos    		Número de cursos que se deben incluir en el horario.
	 * @param numAsignaturas 	Número de asignaturas disponibles para los cursos.
	 * @param horasAsig    		Número de horas asignadas a cada asignatura.
	 * @param numAulas     		Número de aulas disponibles para asignar a los cursos.
	 */
	public void generarHorario(int dias, int periodo, int numCursos, int numAsignaturas, int horasAsig, int numAulas) {
				
		int formatoDias = dias * numAulas;
		horario = new Aula [periodo][formatoDias]; //inicializar la matriz de período x [dias x numAulas]
		//aulas = new Aula [numAulas]; //inicializar el array de aulas
		HashMap<Integer, List<Asignatura>> cursosInicial; //HM auxiliar de todas las asignaturas de cada curso
		
		//Inicializar las asignaturas dependiendo del número de asignaturas. Poner las horas a 0.
		//System.out.println("------------------------------------------------------------------------------------------------------------------------");
		cursosInicial= inicializarAsignaturas(numCursos,numAsignaturas, horasAsig);
		//showCursosAsignaturas(cursosInicial);
		//System.out.println("------------------------------------------------------------------------------------------------------------------------");
		
		//Inicializar la matriz de días por período rellenado cada celda de Aulas (num Aulas)
		for(int i=0;i<periodo;i++) {
			for(int j=0;j<formatoDias;j++) {
				HashMap<Integer, Asignatura> asignaturasPredeterminadas = new HashMap<>();
		        asignaturasPredeterminadas.put(-1, new Asignatura(-1, -1)); // Asignatura predeterminada con id y horas -1
		        horario[i][j] = new Aula(asignaturasPredeterminadas);
			}
		}		
		//recorrerMatrizAulas(numAulas); //mostrar todas las aulas
		//mostrarMatrizHorarios(numAulas);
		//mostrarMatrizCeldas(periodo, formatoDias, numAulas);
		//System.out.println("------------------------------------------------------------------------------------------------------------------------");
		//mostrarHorarioCalendario(numAulas);
		
		//Tengo que meter asignaturas x curso x horasAsig para ver el número de asignaturas que tengo que incluir en el horario
		int iteraciones = numAsignaturas * numCursos * horasAsig; //6*4*4=96
		
		for(int i=0;i<iteraciones;i++) {
			
			//Primer paso: generar 3 aleatorios: uno para ver Aula, otro para el período y otro para ver el curso de la asignatura a introducir
			Random random = new Random();
			int rPeriodo;
			int rDia;
			int rCurso;			
			do {
				rPeriodo = random.nextInt(periodo); //0 a 5
				rDia = random.nextInt(formatoDias); //0 a 29
				rCurso = random.nextInt(numCursos); //0 a 3
			}
			while(!aulaEstaDisponible(rPeriodo, rDia, numAulas, rCurso, cursosInicial));
			//System.out.print("Aula disponible["+(i+1)+"]. Periodo: "+(rPeriodo+1)+", ClaseDia: "+rDia+", Curso: "+rCurso);
			
			//Segundo paso: coger del HashMap cursosInicial, una asignatura aleatoriamente del curso alea 
			//(rCurso), y ponerla en la posición correcta
			List<Asignatura> asignaturasCurso = cursosInicial.get(rCurso);
			
			int indiceAsignatura;
			//la asignatura tiene que tener horas disponibles (numHoras > 0)
			do {
			    indiceAsignatura = random.nextInt(asignaturasCurso.size());
			} while (asignaturasCurso.get(indiceAsignatura).getNumHoras() <= 0);
			//System.out.println(", ID Asignatura: "+indiceAsignatura);
			Asignatura asignaturaSeleccionada = asignaturasCurso.get(indiceAsignatura);
			
			//showCursosAsignaturas(cursosInicial); //Ver que las asignaturas van desapareciendo mientras se van agregando
			asignaturaSeleccionada.setNumHoras(asignaturaSeleccionada.getNumHoras() - 1);
			
			// Tercer paso: asignar la asignatura seleccionada al aula en la posición correspondiente
			horario[rPeriodo][rDia].getCursos().remove(-1); // Eliminar la asignatura predeterminada
			horario[rPeriodo][rDia].getCursos().put(rCurso, asignaturaSeleccionada);
			
			
			//mostrarMatrizCeldas(periodo, formatoDias, numAulas);

		}
		//System.out.println("------------------------------------------------------------------------------------------------------------------------");
		//mostrarAulasConAsignaturas(periodo, formatoDias, numAulas);
		
	}
	/**
	 * Método privado que devuelve true si el aula seleccionada de manera aleatoria está vacía y el curso tiene una asignatura con horas disponibles.
	 * @param rPeriodo			Aleatorio de la hora escogida del aula (fila de la matriz horario)
	 * @param rDia				Aleatorio del día escogido para el aula (columna de la matriz)
	 * @param numAulas			Número de aulas disponibles para asignar a los cursos.
	 * @param rCurso  			Aleatorio para ver si el curso tiene asignaturas disponibles
	 * @param cursosInicial		HashMap de las asignaturas de cada cursos con sus horas para mirar si están disponibles
	 * @return					true si se puede asignar el curso en el aula seleccioanda / false: si el aula ya está seleccionada o no hay asignaturas disponibles del curso pasado por parámetro
	 */
	private boolean aulaEstaDisponible(int rPeriodo, int rDia, int numAulas, int rCurso, HashMap<Integer, List<Asignatura>> cursosInicial) {
	
	    //Ver si el aula está vacía
	    if (!horario[rPeriodo][rDia].getCursos().containsKey(-1)) {
	    	return false;
	    }    
	    // Verificar si ninguna aula del grupo tiene asignado el mismo curso
	    /*int grupoAulas = rDia / numAulas;
	    for (int j = 0; j < numAulas; j++) {
	        int aulaIndex = grupoAulas * numAulas + j;
	        if (horario[rPeriodo][aulaIndex].getCursos().containsKey(rCurso)) {
	            return false; // Al menos una aula tiene asignado el mismo curso
	        }
	    }*/
	    
	 // Verificar si la asignatura seleccionada tiene horas disponibles
	    List<Asignatura> asignaturasCurso = cursosInicial.get(rCurso);
	    for (Asignatura asignatura : asignaturasCurso) {
	        if (asignatura.getNumHoras() > 0) {
	            return true; // Hay al menos una asignatura con horas disponibles
	        }
	    }

	    return false; // Ninguna asignatura tiene horas disponibles
	}

	/**
	 * Método para crear todas las asignaturas de cada curso. Este HM sirve de referencia para ir llenando el hoario.
	 * @param numCursos 			Número de cursos leídos por el archivo config.txt
	 * @param numAsignaturasentero 	Número de asignaturas leídos por el archivo config.txt
	 * @param numHoras				Número de horas de cada asignatura
	 * @return cursos: 				HashMap de cada asignatura por cada curso con sus horas correspondientes
	 */
	public HashMap<Integer, List<Asignatura>> inicializarAsignaturas(int numCursos, int numAsignaturas, int numHoras) {
				
		//System.out.println("Número de cursos: "+numCursos+", Número de Asignaturas: "+numAsignaturas);
		HashMap<Integer, List<Asignatura>> cursos = new HashMap<>();
        
        for (int i = 0; i < numCursos; i++) {
            List<Asignatura> asignaturas = new ArrayList<>();
            for (int j = 0; j < numAsignaturas; j++) {
                Asignatura a = new Asignatura(j, numHoras); // Creo tantas Asig como numAsig y también le añado el número de horas
                asignaturas.add(a);
            }
            cursos.put(i, asignaturas);
        }
        
        return cursos;
	}
	public void recorrerMatrizAulas(int numAulas) {
	    if (horario == null) {
	        System.out.println("La matriz de aulas está vacía.");
	        return;
	    }
	    
	    System.out.println("Recorriendo la matriz de aulas:");
	    int numDias = horario.length / numAulas; // Número de días
	    int numPeriodos = horario[0].length; // Número de periodos por día
	    
	    for (int i = 0; i < numDias; i++) {
	        for (int j = 0; j < numPeriodos; j++) {
	            for (int k = 0; k < numAulas; k++) {
	                int dia = i * numAulas + k; // Obtener el día correspondiente
	                Aula aula = horario[dia][j];
	                System.out.println("Día " + (i + 1) + ", Periodo " + (j + 1) + ", Aula " + (k + 1) + ": " + aula.toString());
	                // Aquí puedes realizar cualquier operación con la aula actual
	            }
	        }
	    }
	}
	public void mostrarMatrizCeldas(int filas, int columnas, int numAulas) {
	    if (horario == null) {
	        System.out.println("La matriz de horarios está vacía.");
	        return;
	    }
	    
	    System.out.println("Número de filas: " + filas + ", Número de columnas: " + columnas);
	    System.out.println("L: Libre, O: ocupada");

	    // Definir los nombres de los días
	    String[] nombresDias = {"Día 1", "Día 2", "Día 3", "Día 4", "Día 5"};

	    // Mostrar los nombres de los días
	    System.out.printf("%-12s", "");
	    for (String nombreDia : nombresDias) {
	        System.out.printf("%-14s", nombreDia); 
	    }
	    System.out.println();

	    for (int i = 0; i < filas; i++) {
	        System.out.printf("Período%-2d:  ", (i + 1)); 
	        for (int j = 0; j < columnas; j++) {
	            Aula aula = horario[i][j];
	            if (aula.getCursos().containsKey(-1)) {
	                System.out.print("L ");
	            } else {
	                System.out.print("O ");
	            }
	            
	            if ((j + 1) % numAulas == 0) {
	                System.out.print("  "); 
	            }
	        }
	        System.out.println(); 
	    }
	}
	/**
	 * Método que muestra por pantalla el horario. Este método hace referencia al fenotipo
	 * @param filas 		Período del horario
	 * @param columnas		Días x numero de aulas
	 * @param numAulas      Aulas por cada día
	 */
	public void mostrarAulasConAsignaturas(int filas, int columnas, int numAulas) {
	    if (horario == null) {
	        System.out.println("La matriz de horarios está vacía.");
	        return;
	    }
	    
	    System.out.println("\nNúmero de filas: " + filas + ", Número de columnas: " + columnas);
	    System.out.println("Contenido de cada aula (Curso, idAsignatura):");

	    // Definir los nombres de los días
	    String[] nombresDias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
	    //String[] nombresDias = {"Día 1", "Día 2", "Día 3", "Día 4", "Día 5"};

	    // Mostrar los nombres de los días
	    System.out.printf("%-3s", "");
	    for (String nombreDia : nombresDias) {
	        System.out.printf("%-44s", nombreDia); 
	    }
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
	    for (int i = 0; i < filas; i++) {
	        System.out.printf("P%-1d:", (i + 1)); 
	        for (int j = 0; j < columnas; j++) {
	            Aula aula = horario[i][j];
	            HashMap<Integer, Asignatura> cursos = aula.getCursos();
	            if (cursos.containsKey(-1)) {
	                System.out.printf("(%2s,%2s)", "", ""); // Aula vacía
	            } else {
	                for (Map.Entry<Integer, Asignatura> entry : cursos.entrySet()) {
	                    int claveCurso = entry.getKey();
	                    int idAsignatura = entry.getValue().getIdAsignatura();
	                    System.out.printf("(%2d,%2d)", claveCurso, idAsignatura);
	                }
	            }
	            if ((j + 1) % numAulas == 0) {
	                System.out.print("  "); 
	            }
	        }
	        System.out.println(); 
	    }
	}

	public Aula[][] getHorario() {
		return horario;
	}

	public void setHorario(Aula[][] horario) {
		this.horario = horario;
	}
	/**
	 * Método para mostrar el HM de cursosInicial por pantalla.
	 * @param todosCursos
	 */
	public void imprimirCursos(HashMap<Integer, List<Asignatura>> todosCursos) {
	    for (Map.Entry<Integer, List<Asignatura>> entry : todosCursos.entrySet()) {
	        Integer key = entry.getKey();
	        List<Asignatura> value = entry.getValue();

	        System.out.print("\nCurso: " + key);
	        for (Asignatura asignatura : value) {
	            System.out.print(" -->IdAsignatura: " + asignatura.getIdAsignatura()+", Horas: "+asignatura.getNumHoras());
	        }
	        
	    }
	}

	
}
