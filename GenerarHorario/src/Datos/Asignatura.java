package Datos;
/**
 * Clase base para representar una asignatura. Tiene como parámetros:
 * - idAsignatura: entero para representar una asignatura de un curso.
 * - numHoras: entero con el número de horas que tiene la asignatura a la semana.
 * @author julian
 */
public class Asignatura {
	private int idAsignatura;
	private int numHoras;
	public Asignatura(int idAsignatura, int numHoras) {
		super();
		this.idAsignatura = idAsignatura;
		this.numHoras = numHoras;
	}
	public int getIdAsignatura() {
		return idAsignatura;
	}
	public void setIdAsignatura(int idAsignatura) {
		this.idAsignatura = idAsignatura;
	}
	public int getNumHoras() {
		return numHoras;
	}
	public void setNumHoras(int numHoras) {
		this.numHoras = numHoras;
	}
	@Override
	public String toString() {
		return "Asignatura [idAsignatura=" + idAsignatura + ", numHoras=" + numHoras + "]";
	}
	
}
