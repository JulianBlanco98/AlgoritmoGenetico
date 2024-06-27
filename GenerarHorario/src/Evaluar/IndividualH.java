package Evaluar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Datos.Asignatura;
import Datos.Aula;
import Datos.Horario;
/**
 * Clase que representa un individuo (horario) de la poblaci�n. Tiene como par�metros:
 * - Horario individiuo: matriz de horario
 * - fitnessValue: entero con el valor de la fitness del individuo
 * @author julian
 *
 */
public class IndividualH {

	private Horario individuo;
	private int fitnessValue;
	
	public IndividualH() {}
	
	public IndividualH(int dias, int periodoDia, int nCursos, int nAsig, int nHoras, int nAulas) {
		
		
		//Inicializar un horario (Constructor por defecto)
		individuo = new Horario();
		
		//Generar el horario del individuo (Clase Horario) con los 6 par�metros
		individuo.generarHorario(dias, periodoDia, nCursos, nAsig, nHoras, nAulas);
	}
	
	public Horario getIndividuo() {
		return individuo;
	}
	public void setIndividuo(Horario individuo) {
		this.individuo = individuo;
	}
	public int getFitnessValue() {
		return fitnessValue;
	}
	public void setFitnessValue(int fitnessValue) {
		this.fitnessValue = fitnessValue;
	}
	/**
	 * M�todo que devuelve un entero con el valor de la fitness despu�s de haberle aplicado los diferentes m�todos de evaluaci�n de la fitness
	 * @param numAulas		Numero de aulas del horario
	 * @param cursos		N�mero de cursos
	 * @param asignaturas	N�mero de asignaturas
	 * @param horas			N�mero de horas
	 * @return 				Entero con el valor de la fitness del individuo
	 */
	public int evaluarFitness(int numAulas, int cursos, int asignaturas, int horas) {
        int fitness = 0;
        //this.individuo.mostrarAulasConAsignaturas(6, 30, 6);
        fitness += verificarTodasClases(cursos, asignaturas, horas);
        fitness += periodoAulaMismoCurso(numAulas);
        fitness += aulaSiguientePeriodoMismaAula();
        //Si la fitness me da negativa, el algoritmo de la ruleta me da error. Por eso, si la fitness es negativa, la pongo a 0
        if(fitness < 0) {
        	fitness = 0;
        }
        //System.out.print(" | Fitness: "+fitness+"\n");
        this.setFitnessValue(fitness);
        return fitness;
    }
	/**
	 * M�todo privado para mejorar la fitness cuando haya una clase en la misma aula que en el periodo anterior
	 * Ejemplo: Periodo 0: (1,1) --> Periodo 1: (1,1) (tienen que estar a su vez en la misma columna (aula de un d�a)
	 * @return entero con el valor de la fitness
	 */
	private int aulaSiguientePeriodoMismaAula() {
		int fitness = 0;
		int filas = this.getIndividuo().getHorario().length;
        int columnas = this.getIndividuo().getHorario()[0].length;
		// Recorro del periodo 0 al pen�ltimo
		for (int i = 0; i < (filas - 1); i++) {
			for (int j = 0; j < columnas; j++) {

				Aula aulaActual = this.getIndividuo().getHorario()[i][j];
				// El aula del curso tiene que tener una clase, es decir, valor diferente de -1
				if (aulaActual.getClaveCurso() != -1) {

					Aula aulaSiguiente = this.getIndividuo().getHorario()[i + 1][j];
					int asigActual = aulaActual.getIdAsignatura();
					int asigSiguiente = aulaSiguiente.getIdAsignatura();
					
					// Compruebo si la siguiente aula tiene el mismo curso y la misma asignatura (1,1) --> (1,1)
					if (aulaActual.getClaveCurso() == aulaSiguiente.getClaveCurso() && asigActual == asigSiguiente) {
						fitness += 1000;
					}
				}

			}
		}
		
		return fitness;
	}
	/**
	 * M�todo privado para evaluar la fitness. De base, pongo 5000 de valor. Si el individuo tiene m�s o menos clases, o tiene las mismas clases pero tiene m�s asignaturas de un curso, resto la fitness 1000.
	 * @param cursos		Numero de cursos
	 * @param asignaturas	Numero de asignaturas
	 * @param horas			Numero de horas
	 * @return				Entero con el valor de la fitness del individuo
	 */
	private int verificarTodasClases(int cursos, int asignaturas, int horas) {
				
		int fitness = 5000;
		int filas = this.getIndividuo().getHorario().length;
        int columnas = this.getIndividuo().getHorario()[0].length;
        //this.individuo.mostrarAulasConAsignaturas(filas, 30, 6);
        
        //int numeroTotalClases = cursos*asignaturas*horas;		
        int contador = 0;
        for(int i=0;i<filas;i++) {
        	for(int j=0;j<columnas;j++) {
        		if(!this.getIndividuo().getHorario()[i][j].getCursos().containsKey(-1)) {
        			contador++;
        		}
        	}
        }
        //System.out.print("N�mero esperado clases: "+numeroTotalClases+" --> N�mero clases horario actual: "+contador);
		
        //Tengo que ver si est�n las clases de cada curso, el n�mero de horas que les corresponde.
        //Ejemplo: Asignatura 1, Curso 2 --> Est� 4 veces (horas = 4)
        
        //Aqu� est�n todos los cursos recogidos de cada curso con sus n�mero de horas
        HashMap<Integer, List<Asignatura>> todosCursos = this.individuo.inicializarAsignaturas(cursos, asignaturas, horas);
        //this.individuo.imprimirCursos(todosCursos);
        
        HashMap<Integer, HashMap<Integer, Integer>> contadorAsignaciones = new HashMap<>();
        
        //Inicializar el HM de contador a 0
		for (int curso = 0; curso < cursos; curso++) {
			contadorAsignaciones.put(curso, new HashMap<>());
			for (Asignatura asignatura : todosCursos.get(curso)) {
				contadorAsignaciones.get(curso).put(asignatura.getIdAsignatura(), 0);
			}
		}

		// Recorro la matriz y actualizar los contadores de cada HM dependiendo de si encuentra la asignatura o no
		for (int i = 0; i < filas; i++) {
			for (int j = 0; j < columnas; j++) {
				Aula aula = this.getIndividuo().getHorario()[i][j];
				for (Map.Entry<Integer, Asignatura> entry : aula.getCursos().entrySet()) {
					int curso = entry.getKey();
					Asignatura asignatura = entry.getValue();
					if (curso != -1) {
						int idAsignatura = asignatura.getIdAsignatura();
						contadorAsignaciones.get(curso).put(idAsignatura, contadorAsignaciones.get(curso).get(idAsignatura) + 1);
					}
				}
			}
		}
		
		// Comparar los contadores con los valores esperados
		for (int curso = 0; curso < cursos; curso++) {
			for (Asignatura asignatura : todosCursos.get(curso)) {
				int idAsignatura = asignatura.getIdAsignatura();
				int numHorasAsignadas = contadorAsignaciones.get(curso).get(idAsignatura);
				int numHorasEsperadas = horas; //4
				//Si es diferente el numHoras de las que hay en el horario, resto la fitness
				if (numHorasAsignadas != numHorasEsperadas) {
					fitness -= Math.abs(numHorasEsperadas - numHorasAsignadas) * 1000; // Penalizar la diferencia
				}
			}
		}
		//Esto es para ver los horarios que tiene todas las clases correctas con sus horas
		/*if(fitness == 5000) {
			System.out.print(" --> Est�n todas las clases");
		}*/
		return fitness;
	}
	/**
	 * Funci�n para evaluar la fitness que mira si en los periodos (6) de cada d�a (6 aulas) hay aulas que tienen el mismo curso. 
	 * @param numAulas	N�mero de aulas
	 * @return 			Entero con el valor de la fitness del individuo
	 */
    private int periodoAulaMismoCurso(int numAulas) {
        int fitness = 0;
        int filas = this.getIndividuo().getHorario().length;
        int columnas = this.getIndividuo().getHorario()[0].length;

        for (int i = 0; i < columnas; i += numAulas) {
            HashMap<Integer, Integer> cursosEnGrupo = new HashMap<>();

            for (int j = 0; j < numAulas; j++) {
                int aulaIndex = i + j;
                for (Map.Entry<Integer, Asignatura> entry : this.getIndividuo().getHorario()[0][aulaIndex].getCursos().entrySet()) {
                    int curso = entry.getKey();
                    if (curso != -1) {
                        cursosEnGrupo.put(curso, cursosEnGrupo.getOrDefault(curso, 0) + 1);
                    }
                }
            }

            boolean coincideCurso = false;

            for (int count : cursosEnGrupo.values()) {
                if (count > 1) {
                    coincideCurso = true;
                    break;
                }
            }

            if (!coincideCurso) {
                fitness += 1000;
            } else {
                fitness -= 4000;
            }
        }

        return fitness;
    }
	public void mostrarHorario_Individuo(int periodo, int dia, int numAulas) {
		
		this.getIndividuo().mostrarAulasConAsignaturas(periodo, dia, numAulas);
		
	}
	/**
	 * M�todo para escoger la mutaci�n dependiendo del par�metro puesto en el archivo Config.txt
	 * @param tipoOperador	Entero para escoger el m�todo de mutaci�n
	 * @param dias			N�mero de d�as del horario
	 * @param aulas			N�mero de aulas del horario
	 */
	public void mutacion (int tipoOperador, int dias, int aulas) {
				
		switch (tipoOperador) {
		case 0: mutacionAdHoc_1();
			break;
		case 1: mutacionAdHoc_2(dias, aulas);			
			break;
		case 2: mutacionDefensa(dias,aulas);
			break;
		default:
			throw new IllegalArgumentException("Valor inesperado: " + tipoOperador);
		}
	}
	private void mutacionDefensa(int dias, int aulas) {
		Random random = new Random();
	    int filas = this.getIndividuo().getHorario().length;
	    int columnas = this.getIndividuo().getHorario()[0].length;

	    //Lista con los 5 d�as
	    List<Integer> diasDisponibles = new ArrayList<>();
	    for (int dia = 0; dia < dias; dia++) {
	        diasDisponibles.add(dia);
	    }
	    
	    //Nuevo horario
	    Aula[][] nuevoHorario = new Aula[filas][columnas];

	    //Recorro por dias
	    for (int dia = 0; dia < dias; dia++) {
	        //Nuevo d�a aleatorio de la lista de dias y borro cuando se coge
	    	//System.out.println("Dias disponibles para mutar: "+diasDisponibles.toString());
	    	int nuevoDia;
	        do {
	            nuevoDia = diasDisponibles.get(random.nextInt(diasDisponibles.size()));
	        } while (nuevoDia == dia && diasDisponibles.size() > 1);
	        
	        System.out.println("Intercambio "+dia+" --> "+nuevoDia);
	        for(int l = 0;l<diasDisponibles.size();l++) {
	        	if(diasDisponibles.get(l) == nuevoDia) {
	        		diasDisponibles.remove(l);
	        	}
	        }
	        
	        // Calculo los �ndices de las columnas correspondientes a los d�as
	        int columnaOrigen = dia * aulas;
	        int columnaDestino = nuevoDia * aulas;

	        // Intercambio las aulas de cada d�a (6 aulas por d�a)
	        for (int aula = 0; aula < aulas; aula++) {
	            for (int fila = 0; fila < filas; fila++) {
	                nuevoHorario[fila][columnaDestino + aula] = this.getIndividuo().getHorario()[fila][columnaOrigen + aula];
	            }
	        }
	    }

	    // Asigno el nuevo horario generado al individuo
	    this.getIndividuo().setHorario(nuevoHorario);
		
		
	}

	/**
	 * Primer m�todo de mutaci�n que cambia un aula por otro. Si coincide ambas aulas, se realiza
	 * el cambio, aunque no se produzca mutaci�n.
	 */
	private void mutacionAdHoc_1() {
		
		Random random = new Random();
		int aula1_Fila = random.nextInt(this.getIndividuo().getHorario().length);
		int aula1_Columna = random.nextInt(this.getIndividuo().getHorario()[0].length);
		
		int aula2_Fila = random.nextInt(this.getIndividuo().getHorario().length);
		int aula2_Columna = random.nextInt(this.getIndividuo().getHorario()[0].length);
		
		/*System.out.println("Aulas Intercambio");
		System.out.println("Aula1 [Periodo: "+(aula1_Fila+1)+", Columna: "+aula1_Columna+"]");
		System.out.println("Aula2 [Periodo: "+(aula2_Fila+1)+", Columna: "+aula2_Columna+"]");
		*/
		Aula aula1 = this.getIndividuo().getHorario()[aula1_Fila][aula1_Columna];
		Aula aula2 = this.getIndividuo().getHorario()[aula2_Fila][aula2_Columna];
		
		//System.out.println("Aula 1 contenido: "+aula1.toString());
		//System.out.println("Aula 2 contenido: "+aula2.toString());
		
		// Intercambiar las aulas
	    this.getIndividuo().getHorario()[aula1_Fila][aula1_Columna] = aula2;
	    this.getIndividuo().getHorario()[aula2_Fila][aula2_Columna] = aula1;
	    
	    // Verificar el intercambio
	    /*System.out.println("Despu�s del intercambio:");
	    System.out.println("Aula 1 nuevo contenido: " + this.getIndividuo().getHorario()[aula1_Fila][aula1_Columna].toString());
	    System.out.println("Aula 2 nuevo contenido: " + this.getIndividuo().getHorario()[aula2_Fila][aula2_Columna].toString());
		*/
	}
	/**
	 * Segundo m�todo de mutaci�n que cambia las aulas de un per�odo de un d�a por otro. Puede coincidir
	 * tanto el d�a como el per�odo, no habr�a mutaci�n.
	 * @param dias		N�mero de d�as del horario
	 * @param numAulas	N�mero de aulas del horario
	 */
	private void mutacionAdHoc_2(int dias, int numAulas) {
		
		Random random = new Random();
		int periodo1 = random.nextInt(this.getIndividuo().getHorario().length);
		int periodo2 = random.nextInt(this.getIndividuo().getHorario().length);;
		
		//Los d�as pueden ser iguales
		int dia1 = random.nextInt(dias);
		int dia2 = random.nextInt(dias);
		
		//Pasar los dias a columnas (multiplicar * numAulas)
		dia1 = dia1 * (numAulas);
		dia2 = dia2 * (numAulas);
		
		//System.out.println("Eleccion1 --> Periodo: "+(periodo1+1)+", Dia: "+dia1+"("+(dia1/numAulas)+")");
		//System.out.println("Eleccion2 --> Periodo: "+(periodo2+1)+", Dia: "+dia2+"("+(dia2/numAulas)+")");
		
		Aula[] aulas1 = new Aula[numAulas];
		Aula[] aulas2 = new Aula[numAulas];
		
		//Coger las n aulas de ambos periodos
		for(int i=0;i<numAulas;i++) {
			aulas1[i] = this.getIndividuo().getHorario()[periodo1][dia1+i];
			aulas2[i] = this.getIndividuo().getHorario()[periodo2][dia2+i];
		}
		
		/*mostrarPeriodoAula(numAulas, aulas1);
		mostrarPeriodoAula(numAulas, aulas2);*/
		
		//Hacer la mutacion del cambio de periodo
		for(int i=0;i<numAulas;i++) {
			this.getIndividuo().getHorario()[periodo1][dia1+i] = aulas2[i];
			this.getIndividuo().getHorario()[periodo2][dia2+i] = aulas1[i];
		}
		
		
		
		
	}
	public int contarTotalClases() {
		int contador = 0;
		int filas = this.getIndividuo().getHorario().length;
        int columnas = this.getIndividuo().getHorario()[0].length;
        for(int i=0;i<filas;i++) {
        	for(int j=0;j<columnas;j++) {
        		if(!this.getIndividuo().getHorario()[i][j].getCursos().containsKey(-1)) {
        			contador++;
        		}
        	}
        }
        return contador;
	}
}
