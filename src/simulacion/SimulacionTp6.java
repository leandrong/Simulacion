package simulacion;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class SimulacionTp6 {
	
	static Date t, tf, tpll, menorTcm;
	static int mCantMotos; //M
	static int p; //tipo de pedido
	static int tc; //tiempo comprometido
	static Date[] tcc, tcm;
	static int indiceMenorTcc, indiceMenorTcm;
	static int iA, tV;
	static int sto = 0;
	static int stv = 0;
	
	public static void main(String arg[]){
		
		float r,k; //random
		tcc = new Date[8];	//tiempo comprometido cocina
		Date menorTcc;
		int tp1 = 0; 
		int tp2 = 0; 
		int tp3 = 0; 
		int tp4 = 0;
		Date tcc1 = new Date();
		Date tcc2 = new Date(); 
		Date tcc3 = new Date();
		Date tcc4 = new Date();
		int tv1 = 0;
		int tv2 = 0;
		int tv3 = 0;
		int tv4 = 0;
		
		try {
			condicionesIniciales();
			
			do {
				iA = getIntervaloArribo();
				tpll = sumarMinutos(t,iA);
				t = tpll;
				r = getRandom();
	
				setTipoPedidoYTc(r);
				
				menorTcc = getMenorTcx(tcc,indiceMenorTcc);
				
				if (t.before(menorTcc)) {	
					//lado NO del diagrama con condicion t >= tcc(y)
					tcc[indiceMenorTcc] = sumarMinutos(tcc[indiceMenorTcc], tc);
				} else {
					//lado SI del diagrama con condicion t >= tcc(y)
					tcc[indiceMenorTcc] = sumarMinutos(t, tc);
				}
				
				tV = getTiempoViaje();
				
				k = getRandom();
				
				if (k<=0.14) {
					//zona3
					actualizarVariables(tp3,tcc3,tv3);
				} else if (k<=0.23) {
					//zona4
					actualizarVariables(tp4,tcc4,tv4);
				} else if (k <= 0.30) {
					//zona1
					actualizarVariables(tp1,tcc1,tv1);
				} else {
					//zona2
					actualizarVariables(tp2,tcc2,tv2);
				}
				
			} while (t.before(tf));
			
			//mostrar resultados
			mostrarResultados();
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void mostrarResultados() {
		String formatoHoras="HH";
		String formatoMinutos="mm";
		SimpleDateFormat dateFormatMinutes = new SimpleDateFormat(formatoMinutos);
		SimpleDateFormat dateFormatHours = new SimpleDateFormat(formatoHoras);
		
		//paso el t a minutos ya que el sto es sumatoria de minutos
		int horasDeT = Integer.parseInt(dateFormatHours.format(t));
		int minutosDeT = Integer.parseInt(dateFormatMinutes.format(t));
		
		//pto
		float pto = (float)sto / (float)((horasDeT*60) + minutosDeT);
		//ptv
		float ptv = (float)stv / (float)((horasDeT*60) + minutosDeT);
		
		DecimalFormat decimales = new DecimalFormat("0.00");
		
		System.out.println("M: " + mCantMotos);
		System.out.println("PTO: " + decimales.format(pto));
		System.out.println("PTV: " + decimales.format(ptv));
		
	}

	private static int restarMinutos(Date date, Date tcc3) {
		//TODO testear. y en los casos que tcc3 > date ? esto es posible?
		// si eso es posible hay q modificar la logica, ya que
		//Por ejemplo: 07:45:00 - 07:55:00 = 06:50:00 => la funcion esta retornando el numero 50.
		
		String formato="mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date); // Configuramos la fecha que se recibe
		calendar.add(Calendar.MINUTE, Integer.parseInt(dateFormat.format(tcc3))*-1);  // numero de minutos a restar (por eso el * -1)
		
		return Integer.parseInt(dateFormat.format(calendar.getTime()));
	}

	private static int getTiempoViaje() {
		// TODO Auto-generated method stub
		return 20; //FIXME hardcodeo
	}

	private static Date getMenorTcx(Date[] arrayTcx, int indiceMenorTcx) {
		Date menor = arrayTcx[0];
		indiceMenorTcx = 0;
		for (int i=0; i<arrayTcx.length; i++) {
			if (arrayTcx[i].before(menor)) {
				menor = arrayTcx[i];
				indiceMenorTcx = i;
			}
		}
		return menor;
	}

	private static void setTipoPedidoYTc(float r) {
		if (r<= 0.25) {
			p = 2;
			tc = 20;
		} else if (r <= 0.35) {
			p = 1;
			tc = 15;
		} else {
			p = 4;
			tc = 25;
		}
	}

	private static float getRandom() {
		Random rnd = new Random();
		return rnd.nextFloat();
	}

	private static Date sumarMinutos(Date t, int iA) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(t); // Configuramos la fecha que se recibe
		calendar.add(Calendar.MINUTE, iA);  // numero de minutos a añadir
		
		return calendar.getTime();
	}

	public static void condicionesIniciales() throws ParseException {
		
		System.out.print("Ingrese cantidad de motos: ");
		Scanner sc = new Scanner(System.in);
		mCantMotos = sc.nextInt();	//M
		
		tcm = new Date[mCantMotos-1];
		
		System.out.print("Ingrese hora de inicio (hh:mm:ss): ");
		String horaInicio = sc.next();
		
		System.out.print("Ingrese hora fin (hh:mm:ss): ");
		String horaFin = sc.next();
		
		SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm:ss");
		t = formatoFecha.parse(horaInicio);
		tf = formatoFecha.parse(horaFin);
		
		//inicio array tcc y tcm
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        String formato="HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
		String horaCero = dateFormat.format(calendar.getTime());
        
		for (int i=0; i<tcc.length; i++) {
			tcc[i] = formatoFecha.parse(horaCero);
		}
		
		for (int i=0; i<tcm.length; i++) {
			tcm[i] = formatoFecha.parse(horaCero);
		}
		
		sc.close();
	}

	private static int getIntervaloArribo() {
		// TODO Auto-generated method stub
		//retorna el iA en minutos
		return 15;	//FIXME hardcodeo
	}
	
	private static void actualizarVariables(int tpX, Date tccX, int tvX) {
		if (tpX==0) {
			// primer pedido de la nueva entrega
			tpX = p;
			tccX = tcc[indiceMenorTcc];
			tvX = tV;
		} else {
			if ((tpX * p <= 4) && (restarMinutos(tcc[indiceMenorTcc],tccX) < 15)) {
				tpX += p;
				if (tcc[indiceMenorTcc].after(tccX)) {	// pedido que mas tarda
					tccX = tcc[indiceMenorTcc];
				}
				if(tV>tvX) {	// pedido mas lejos
					tvX = tV;
				}
			} else {
				menorTcm = getMenorTcx(tcm,indiceMenorTcm);
				if (t.before(menorTcm)) {
					// lado NO del diagrama con condicion t >= tcm(x)
					tcc[indiceMenorTcc] = sumarMinutos(tcc[indiceMenorTcm], tc);
				} else {
					// lado SI del diagrama con condicion t >= tcm(x)
					sto += restarMinutos(t,tcm[indiceMenorTcm]);
					tcm[indiceMenorTcm] = sumarMinutos(t, tc);
					stv += tvX;
				}
				tpX = p;
				tccX = tcc[indiceMenorTcc];
				tvX = tV;
			}
		}
		
		if (tpX == 4) {
			menorTcm = getMenorTcx(tcm,indiceMenorTcm);
			if (t.before(menorTcm)) {
				// lado NO del diagrama con condicion t >= tcm(x)
				tcc[indiceMenorTcc] = sumarMinutos(tcc[indiceMenorTcm], tc);
			} else {
				// lado SI del diagrama con condicion t >= tcm(x)
				sto += restarMinutos(t,tcm[indiceMenorTcm]);
				tcm[indiceMenorTcm] = sumarMinutos(t, tc);
				stv += tvX;
			}
		}
	}
}
