package Datos;

import java.util.HashMap;
import java.util.List;
/**
 * Clase que representa un aula en el horario. Tiene como parámetro:
 * - HashMap <Integer, Asignatura> cursos: cada aula tiene un mapa con clave el curso y de valor, un objeto asignatura
 * Ejemplo de aula: cursos (1, 3) --> En este aula, se imparte la asignatura 3 del curso 1.
 * @author julian
 *
 */
public class Aula {
	//Cada aula va a tener un curso con una asignatura
	private HashMap<Integer, Asignatura> cursos; 

	public Aula(HashMap<Integer, Asignatura> cursos) {
		super();
		this.cursos = cursos;
	}

	public HashMap<Integer, Asignatura> getCursos() {
		return cursos;
	}

	public void setCursos(HashMap<Integer, Asignatura> cursos) {
		this.cursos = cursos;
	} 
	/**
	 * Método para devolver la primera clave del HashMap cursos. Este método lo uso para evaluar la fitness
	 * @return el entero del curso que busco
	 */
    public Integer getClaveCurso() {
        if (cursos != null && !cursos.isEmpty()) {
            // Devuelve la primera clave del HashMap
            return cursos.keySet().iterator().next();
        }
        return null; // O podrías lanzar una excepción si no hay claves
    }
    /**
     * Método para devolver el idAsignatura del primer curso en el HashMap cursos. Este método lo uso para evaluar la fitness
     * @return el entero de la asignatura del curso
     */
    public Integer getIdAsignatura() {
        if (cursos != null && !cursos.isEmpty()) {
            // Obtiene la primera clave del HashMap
            Integer clave = cursos.keySet().iterator().next();
            // Devuelve el idAsignatura correspondiente a esa clave
            Asignatura asignatura = cursos.get(clave);
            if (asignatura != null) {
                return asignatura.getIdAsignatura();
            }
        }
        return null; 
    }
	


	@Override
	public String toString() {
		String result = "[ ";
	    for (Integer curso : cursos.keySet()) {
	        Asignatura asignatura = cursos.get(curso);
	        result += "Curso: " + curso + ", ";
	        result += "ID de Asignatura: " + asignatura.getIdAsignatura();
	       // result += "Número de horas: " + asignatura.getNumHoras() + "";
	    }
	    result += " ]";
	    return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cursos == null) ? 0 : cursos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Aula other = (Aula) obj;
		if (cursos == null) {
			if (other.cursos != null)
				return false;
		} else if (!cursos.equals(other.cursos))
			return false;
		return true;
	}
		
}
