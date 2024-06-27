package Evaluar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import Datos.Aula;
import Datos.Horario;
/**
 * Clase que representa la población (n individuos) del algoritmo genético. Tiene como parámetros:
 * - Double totalFitness: suma del valor de todas las fitness de los individuos de la población.
 * - Individual[] horariosTotal: array con todos los individuos de la población
 * @author julian
 *
 */
public class PoblacionH {

	private double totalFitness;
	private IndividualH [] horariosTotal; //Matriz de horarios
	
	private int poblacion; //Tamaño población
	private int elitismo; //elitismo
	private float probCruce; //probabilidad de cruce
	private float probMutacion; //probabilidad de mutación
	private int numGeneraciones; //Número máximo de iteracciones
	private int aulas;
	
	
	//Semilla de aleatorios
	private static Random m_rand = new Random();
	
	public PoblacionH(int numeroCursos, int numeroAsignaturas, int numeroHorasAsignatura, int aulas, int dias, int periodoDia,
            int poblacion, int numGeneraciones, float probCruce, float probMutacion, int elitismo, int estrategiaRemplazamiento,
            int operadorCruce, int operadorMutacion, int selectorAlgoritmo) {
        
		this.elitismo = elitismo;
		this.poblacion = poblacion;
		this.probCruce = probCruce;
		this.probMutacion = probMutacion;
		this.numGeneraciones = numGeneraciones;
		this.aulas = aulas;
		
	      
       horariosTotal = new IndividualH[poblacion]; //array de horarios (depende del parámetro de población de "Config.txt"
       
       //Iniciar la poblacion y generar horario (lo hago en la clase IndividualH)
       for(int i=0;i<poblacion;i++) {
    	   horariosTotal[i] = new IndividualH(dias, periodoDia, numeroCursos, numeroAsignaturas, numeroHorasAsignatura, aulas);    	   
       }
       
       //Evaluar la población actual
       this.evaluate(numeroCursos, numeroAsignaturas, numeroHorasAsignatura);
	
	}
	/**
	 * Método que devuelve la suma de todas las fitness de los individuos de la población
	 * @param curso			Numero de cursos
	 * @param asignaturas	Numero de asignaturas
	 * @param horas			Numero de horas
	 * @return valor total de la fitness
	 */
	public double evaluate(int curso, int asignaturas, int horas) {
		this.totalFitness = 0.0d;
		for(int i=0;i< poblacion;i++) {
			this.totalFitness = this.totalFitness + horariosTotal[i].evaluarFitness(aulas, curso, asignaturas, horas);			
		}
		
		return this.totalFitness;
	}
	/**
	 * Método que devuelve el valor medio de la fitness de la poblacion
	 * @return valor medio de la fitness
	 */
	public double calcularMediaFitness() {
        double sumaFitness = 0.0;
        for (IndividualH individuo : horariosTotal) {
            sumaFitness += individuo.getFitnessValue();
        }
        return sumaFitness / poblacion;
    }
	/**
	 * Método que devuelve el mejor individuo de la población según su fitness
	 * @return
	 */
	public IndividualH findBestIndividual() {
		
		int idxMax = 0, idxMin = 0;
	    double currentMax = 0.0;
	    double currentMin = 1.0;
	    double currentVal;

	    for (int idx = 0; idx < poblacion; ++idx) {
	        currentVal = horariosTotal[idx].getFitnessValue();
	        if (currentMax < currentVal) {
	            currentMax = currentVal;
	            idxMax = idx;
	        }
	        if (currentVal < currentMin) {
	            currentMin = currentVal;
	            idxMin = idx;
	        }
	    }

	    // return horariosTotal[idxMin]; // Si se quiere minimizar
	    return horariosTotal[idxMax]; // Si se quiere maximizar
	}
	
	public double getm_rand() {
		return this.m_rand.nextDouble();
	}
	
	public double getTotalFitness() {
		return totalFitness;
	}
	public void setTotalFitness(double totalFitness) {
		this.totalFitness = totalFitness;
	}
	/**
	 * Método que devuelve un individuo siguiendo el algoritmo de la ruleta
	 * @return individuo seleccionado por la ruleta
	 */
	public IndividualH selectorPorRuleta() {
		double randNum = m_rand.nextDouble() * totalFitness;
        int idx;
        for (idx = 0; idx < horariosTotal.length && randNum > 0; ++idx) {
            randNum -= horariosTotal[idx].getFitnessValue();
        }
        return horariosTotal[idx - 1];
    }
	/**
	 * Método que deveulve un individuo según el método de seleccion por ranking
	 * @return individuo seleccionado
	 */
	public IndividualH selectorPorRanking() {
	    // Ordenar la población por fitness en orden descendente
	    IndividualH[] sortedPopulation = Arrays.copyOf(horariosTotal, horariosTotal.length);
	    Arrays.sort(sortedPopulation, Comparator.comparingDouble(IndividualH::getFitnessValue).reversed());

	    // Calcular las probabilidades de selección basadas en el ranking
	    double[] selectionProbabilities = new double[poblacion];
	    double totalRank = (poblacion * (poblacion + 1)) / 2.0; // Suma de la serie 1 + 2 + ... + n

	    for (int i = 0; i < poblacion; i++) {
	        selectionProbabilities[i] = (poblacion - i) / totalRank;
	    }

	    // Seleccionar un individuo basándose en las probabilidades
	    double randNum = m_rand.nextDouble();
	    double cumulativeProbability = 0.0;

	    for (int i = 0; i < poblacion; i++) {
	        cumulativeProbability += selectionProbabilities[i];
	        if (randNum <= cumulativeProbability) {
	            return sortedPopulation[i];
	        }
	    }

	    return sortedPopulation[poblacion - 1]; // Por si acaso no se selecciona en el bucle
	}
	/**
	 * Método que devuelve un individuo según el método de selección por torneo
	 * @return individuo seleccionado
	 */
	public IndividualH selectorPorTorneo() {
		//Primera pareja: competidores
		int jugador1 = m_rand.nextInt(poblacion);
		int jugador2 = m_rand.nextInt(poblacion);
		
		//Segunda pareja: competidores
		int jugador3 = m_rand.nextInt(poblacion);
		int jugador4 = m_rand.nextInt(poblacion);
		
		IndividualH pareja1, pareja2;
		if (horariosTotal[jugador1].getFitnessValue() > horariosTotal[jugador2].getFitnessValue()) {
		    pareja1 = horariosTotal[jugador1];
		} else {
		    pareja1 = horariosTotal[jugador2];
		}
		if (horariosTotal[jugador3].getFitnessValue() > horariosTotal[jugador4].getFitnessValue()) {
		    pareja2 = horariosTotal[jugador3];
		} else {
		    pareja2 = horariosTotal[jugador4];
		}
		
		//Se devuelve el que mayor fitness tenga de los ganadores
		return pareja1.getFitnessValue() > pareja2.getFitnessValue() ? pareja1 : pareja2;
	}
	/**
	 * Método que devuelve un individuo según el método de selección truncada
	 * @param numeroDescartes	Numero que representa los descartes de la población (15% de la poblacion)
	 * @return individuo seleccionado
	 */
	public IndividualH selectorTruncada(int numeroDescartes) {
		
		IndividualH[] sortedPopulation = Arrays.copyOf(horariosTotal, horariosTotal.length);
	    Arrays.sort(sortedPopulation, Comparator.comparingDouble(IndividualH::getFitnessValue).reversed());
	    
	    // Convertir la población ordenada a una lista
	    List<IndividualH> listaIndividuos = new ArrayList<>(Arrays.asList(sortedPopulation));
	    
	    // Descartar los n individuos con menor fitness
	    List<IndividualH> individuosRestantes = listaIndividuos.subList(0, listaIndividuos.size() - (listaIndividuos.size() - numeroDescartes));
	    
        //Genero un aleatorio entre 0 y size del número de individuos aptos
        int individuoSeleccioando = m_rand.nextInt(individuosRestantes.size());
        
        return individuosRestantes.get(individuoSeleccioando);
		
	}
	


	/**
	 * Mñetodo que devuelve los dos individuos después de aplicarle el método de cruce según el parámetro operadorCruce
	 * @param padre1			Individuo padre1
	 * @param padre2			Individuo padre2
	 * @param operadorCruce		Número que representa el operador de cruce que se va a aplicar
	 * @param dias				Número de días (lo necesito para los métodos de cruce)
	 * @return					Los dos individuos con el método de cruce aplicado
	 */
	public IndividualH[] cruzarHorarios(IndividualH padre1, IndividualH padre2, int operadorCruce, int dias) {
	    switch (operadorCruce) {
	        case 0:
	            return cruzarEnUnPunto(padre1, padre2, dias);
	        case 1:
	            return cruzarMultipunto(padre1, padre2, dias);
	        case 2:
	            return cruzarUniforme(padre1, padre2, dias);
	        default:
	            throw new IllegalArgumentException("Operador de cruce no válido.");
	    }
	}

	/**
	 * Método de cruce en un punto que devuelve los dos individuos con el cruce hecho
	 * @param padre1		Individuo padre1
	 * @param padre2		Individuo padre2
	 * @param dias			Numero de dias
	 * @return				Los dos individuos con el método de cruce aplicado
	 */
	private IndividualH[] cruzarEnUnPunto(IndividualH padre1, IndividualH padre2, int dias) {
	    //Random random = new Random();
	    // Crear nuevos individuos para los hijos
	    IndividualH hijo1 = new IndividualH();
	    IndividualH hijo2 = new IndividualH();
	    
	    Aula[][] horarioPadre1 = padre1.getIndividuo().getHorario();
	    Aula[][] horarioPadre2 = padre2.getIndividuo().getHorario();
	    Aula[][] horarioHijo1 = new Aula[horarioPadre1.length][horarioPadre1[0].length];
	    Aula[][] horarioHijo2 = new Aula[horarioPadre1.length][horarioPadre1[0].length];
	        
	    // Seleccionar un punto de cruce aleatorio (0 a numDias (columnas))
	    int puntoCruce = m_rand.nextInt(dias);
	    //System.out.println("Día de la semana escogido para el punto:"+puntoCruce);
	    
	    //Calculo: (Aulas) * Punto+1 (día del punto incluido)	    
		// Copiar las columnas del horario del padre a su hijo correspondiente
		for (int i = 0; i < horarioPadre1.length; i++) {
			System.arraycopy(horarioPadre1[i], 0, horarioHijo1[i], 0, (aulas*(puntoCruce+1)));
			System.arraycopy(horarioPadre2[i], 0, horarioHijo2[i], 0, (aulas*(puntoCruce+1)));
		}
	    
		//Solo hago el cruce si el punto es distinto de 4 (Viernes)
		if(puntoCruce != 4) {
			
			//Calculo: empezar en el siguiente día: puntoCruce + 1 * aulas
			//Numero de elementos a copiar: (NumColumnas-((punto+1)*aulas))
			
			// Copiar la segunda parte de las columnas del horario del padre al hijo contrario
			for (int i = 0; i < horarioPadre1.length; i++) {
				System.arraycopy(horarioPadre2[i], ((puntoCruce+1)*aulas), horarioHijo1[i], ((puntoCruce+1)*aulas), (horarioPadre1[0].length-((puntoCruce+1)*aulas)));
				System.arraycopy(horarioPadre1[i], ((puntoCruce+1)*aulas), horarioHijo2[i], ((puntoCruce+1)*aulas), (horarioPadre1[0].length-((puntoCruce+1)*aulas)));
			}
		}
			    
	    hijo1.setIndividuo(new Horario(horarioHijo1));
	    hijo2.setIndividuo(new Horario(horarioHijo2));
	    
	    return new IndividualH[] { hijo1, hijo2 };
	}
	/**
	 * Método de cruce multipunto con dos puntos aleatorios
	 * @param padre1		Individuo padre1
	 * @param padre2		Individuo padre2
	 * @param dias			Numero de dias
	 * @return				Los dos individuos con el método de cruce aplicado
	 */
	private IndividualH[] cruzarMultipunto(IndividualH padre1, IndividualH padre2, int dias) {
	    // Seleccionar dos puntos de cruce aleatorios.
		//No voy a poner el extremo derecho del cromosoma (Viernes), para que se creen los 3 segmentos
		//y haya más diversidad en ambas poblaciones
	    int puntoCruce1 = m_rand.nextInt((dias-1));
	    int puntoCruce2 = m_rand.nextInt((dias-1));
	    
	    // Asegurar que los puntos de cruce sean diferentes
	    while (puntoCruce1 == puntoCruce2) {
	        puntoCruce2 = m_rand.nextInt((dias-1));
	    }
	    	    
	    // Ordenar los puntos de cruce de menor a mayor
	    int puntoMenor = Math.min(puntoCruce1, puntoCruce2);
	    int puntoMayor = Math.max(puntoCruce1, puntoCruce2);
	    
	    //System.out.println("Punto menor: "+puntoMenor);
	    //System.out.println("Punto mayor: "+puntoMayor);
	    
	    // Crear nuevos individuos para los hijos
	    IndividualH hijo1 = new IndividualH();
	    IndividualH hijo2 = new IndividualH();
	    
	    Aula[][] horarioPadre1 = padre1.getIndividuo().getHorario();
	    Aula[][] horarioPadre2 = padre2.getIndividuo().getHorario();
	    Aula[][] horarioHijo1 = new Aula[horarioPadre1.length][horarioPadre1[0].length];
	    Aula[][] horarioHijo2 = new Aula[horarioPadre1.length][horarioPadre1[0].length];
	    
	    // Copiar las partes del horario de los padres a los hijos
	    
	    //Primera parte: no hay cruce. De 0 a puntoMenor
	    for (int i = 0; i < horarioPadre1.length; i++) {
	    	System.arraycopy(horarioPadre1[i], 0, horarioHijo1[i], 0, ((puntoMenor+1)*aulas));
	    	System.arraycopy(horarioPadre2[i], 0, horarioHijo2[i], 0, ((puntoMenor+1)*aulas));	    	
	    }
	    
	    //Segunda parte: cruce. De puntoMenor a puntoMayor
	    for (int i = 0; i < horarioPadre1.length; i++) {
	    	System.arraycopy(horarioPadre2[i], ((puntoMenor+1)*aulas), horarioHijo1[i], ((puntoMenor+1)*aulas), ((puntoMayor+1) - (puntoMenor+1))*aulas);
	    	System.arraycopy(horarioPadre1[i], ((puntoMenor+1)*aulas), horarioHijo2[i], ((puntoMenor+1)*aulas), ((puntoMayor+1) - (puntoMenor+1))*aulas);	    	
	    }
	    
	    //Tercera parte: no hay cruce. 
	    for (int i = 0; i < horarioPadre1.length; i++) {
	    	System.arraycopy(horarioPadre1[i], ((puntoMayor+1)*aulas), horarioHijo1[i], ((puntoMayor+1)*aulas), (((dias-1) - (puntoMayor))*aulas));
	    	System.arraycopy(horarioPadre2[i], ((puntoMayor+1)*aulas), horarioHijo2[i], ((puntoMayor+1)*aulas), (((dias-1) - (puntoMayor))*aulas));	    	
	    }
	    	    
	    hijo1.setIndividuo(new Horario(horarioHijo1));
	    hijo2.setIndividuo(new Horario(horarioHijo2));
	    
	    return new IndividualH[] { hijo1, hijo2 };
	}
	/**
	 * Método de cruce uniforme
	 * @param padre1		Individuo padre1	
	 * @param padre2		Individuo padre2
	 * @param dias			Numero de dias
	 * @return				Los dos individuos con el método de cruce aplicado
	 */
	private IndividualH[] cruzarUniforme(IndividualH padre1, IndividualH padre2, int dias) {
		
	    // Crear nuevos individuos para los hijos
	    IndividualH hijo1 = new IndividualH();
	    IndividualH hijo2 = new IndividualH();
	    
	    Aula[][] horarioPadre1 = padre1.getIndividuo().getHorario();
	    Aula[][] horarioPadre2 = padre2.getIndividuo().getHorario();
	    Aula[][] horarioHijo1 = new Aula[horarioPadre1.length][horarioPadre1[0].length];
	    Aula[][] horarioHijo2 = new Aula[horarioPadre1.length][horarioPadre1[0].length];
	    
	    //System.out.println();
	 // Copiar los genes (dias) del horario de los padres a los hijos
	    for (int dia = 0; dia < dias; dia++) {
	    	//Aleatorio para saber si va a haber cruce ese día o no
	        boolean opcion = m_rand.nextBoolean();
	        //System.out.print("Dia "+dia+": " + (opcion ? "Cruce" : "No Cruce")+" | ");
	        
	        //Calculo: dia * aula --> Inicio para saber donde copiar
	        //Numero de elementos a copiar: aula. Las aulas disponibles
	        
	        // True: intercambiar las columnas correspondientes a este día entre los padres
	    	if (opcion) {
	            for (int i = 0; i < horarioPadre1.length; i++) {
	                // Copiar de padre2 a hijo1 y de padre1 a hijo2
	                System.arraycopy(horarioPadre2[i], dia * aulas, horarioHijo1[i], dia * aulas, aulas);
	                System.arraycopy(horarioPadre1[i], dia * aulas, horarioHijo2[i], dia * aulas, aulas);
	            }
	        } else {
	            // False: copiar las columnas correspondientes a este día sin intercambio
	            for (int i = 0; i < horarioPadre1.length; i++) {
	                // Copiar de padre1 a hijo1 y de padre2 a hijo2
	                System.arraycopy(horarioPadre1[i], dia * aulas, horarioHijo1[i], dia * aulas, aulas);
	                System.arraycopy(horarioPadre2[i], dia * aulas, horarioHijo2[i], dia * aulas, aulas);
	            }
	        }
	    }
	    
	    hijo1.setIndividuo(new Horario(horarioHijo1));
	    hijo2.setIndividuo(new Horario(horarioHijo2));
	    
	    return new IndividualH[] { hijo1, hijo2 };
	}
	/**
	 * Método que copia la nueva población en la actual
	 * @param newPop
	 */
	public void setPopulation(IndividualH[] newPop) {
	    // Copiar la nueva población a la población actual
	    System.arraycopy(newPop, 0, this.horariosTotal, 0, poblacion);
	}
	public IndividualH[] getHorariosTotal() {
		return horariosTotal;
	}
	public void setHorariosTotal(IndividualH[] horariosTotal) {
		this.horariosTotal = horariosTotal;
	}
	/**
	 * Método que devuelve los mejores individuos dependiendo del parámetro elitismo
	 * @return array con los mejores individuos
	 */
	public IndividualH[] getTopElites() {
		// Ordenar la población por fitness en orden descendente
		IndividualH[] sortedPopulation = Arrays.copyOf(horariosTotal, horariosTotal.length);
		Arrays.sort(sortedPopulation, Comparator.comparingDouble(IndividualH::getFitnessValue).reversed());

		// Crear un array para los mejores individuos
		IndividualH[] elites = new IndividualH[elitismo];
		System.arraycopy(sortedPopulation, 0, elites, 0, elitismo);

		return elites;
	}
	/**
	 * Método para mostrar la fitness de la población por pantalla
	 */
	public void mostrarFitnessPoblacion() {
	    for (int i = 0; i < horariosTotal.length; i++) {
	        System.out.println("Individuo " + i + ", fitness: " + horariosTotal[i].getFitnessValue());
	    }
	}

	


	
	
}
