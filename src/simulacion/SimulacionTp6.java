package simulacion;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class SimulacionTp6 {
	
	private static final String HORA_CERO = "00:00:00";
	private static final String HORA_INICIAL_TCM_TCB = "20:15:00";
	private static final String HORA_FINAL = "21:00:00";
	private static final String HORA_INICIAL = "20:00:00";
	static Date t, tf, tpll, menorTcm;
	static int mCantMotos; //M
	static int bCantBicicletas; //B
	static int p; //tipo de pedido
	static int tc; //tiempo comprometido
	static Date[] tcc, tcm, tcb, stom, stob;
	static int indiceMenorTcc, indiceMenorTcm, indiceMenorTcb;
	static int iA, tV;
	static int sto = 0;
	static int nt = 0;
	static int str = 0;
	
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
				t = tpll;
				iA = getIntervaloArribo();
				tpll = sumarMinutos(t,iA);
				r = getRandom();
	
				setTipoPedidoYTc(r);
				
				indiceMenorTcc = getMenorTcx(tcc);
				
				if (t.before(tcc[indiceMenorTcc])) {	
					//lado NO del diagrama con condicion t >= tcc(y)
					tcc[indiceMenorTcc] = sumarMinutos(tcc[indiceMenorTcc], tc);
				} else {
					//lado SI del diagrama con condicion t >= tcc(y)
					tcc[indiceMenorTcc] = sumarMinutos(t, tc);
				}
				
				tV = getTiempoViaje();
				
				//k = getRandom();
				
				nt += 1;
				
				if (tV >10 || p==2) {
					//moto
					indiceMenorTcm = getMenorTcx(tcm);
					if (tcc[indiceMenorTcc].before(tcm[indiceMenorTcm])) {
						//lado NO
						tcm[indiceMenorTcm] = sumarMinutos(tcm[indiceMenorTcm], tV);
					} else {
						//lado SI
						stom[indiceMenorTcm] = sumarMinutos(stom[indiceMenorTcm], restarMinutos(tcc[indiceMenorTcc], tcm[indiceMenorTcm]));
						tcm[indiceMenorTcm] = sumarMinutos(tcc[indiceMenorTcc], tV);
					}
					calcularStrConTcm();
				} else {
					//bici o moto
					indiceMenorTcb = getMenorTcx(tcb);
					indiceMenorTcm = getMenorTcx(tcm);
					if (tcb[indiceMenorTcb].after(tcm[indiceMenorTcm])) {
						//lado NO
						if (tcc[indiceMenorTcc].before(tcm[indiceMenorTcm])) {
							//lado NO
							tcm[indiceMenorTcm] = sumarMinutos(tcm[indiceMenorTcm], tV);
						} else {
							//lado SI
							stom[indiceMenorTcm] = sumarMinutos(stom[indiceMenorTcm], restarMinutos(tcc[indiceMenorTcc], tcm[indiceMenorTcm]));
							tcm[indiceMenorTcm] = sumarMinutos(tcc[indiceMenorTcc], tV);
						}
						calcularStrConTcm();
					} else {
						//lado SI
						if (tcc[indiceMenorTcc].before(tcb[indiceMenorTcb])) {
							//lado NO
							tcb[indiceMenorTcb] = sumarMinutos(tcb[indiceMenorTcb], tV);
						} else {
							//lado SI
							stob[indiceMenorTcb] = sumarMinutos(stob[indiceMenorTcb], restarMinutos(tcc[indiceMenorTcc], tcb[indiceMenorTcb]));
							tcb[indiceMenorTcb] = sumarMinutos(tcc[indiceMenorTcc], tV);
						}
						calcularStrConTcb();
					}
				}
				
			} while (t.before(tf));
			
			calcularTORestante();
			
			//mostrar resultados
			mostrarResultados();
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void calcularTORestante() {
		
		for(int i=0; i<tcm.length; i++) {
			if (tcm[i].before(tf)) {
				stom[i] = sumarMinutos(stom[i], restarMinutos(tf, tcm[i]));
			}
		}
	}

	private static void calcularStrConTcb() {
		String formatoHoras="HH";
		String formatoMinutos="mm";
		SimpleDateFormat dateFormatMinutes = new SimpleDateFormat(formatoMinutos);
		SimpleDateFormat dateFormatHours = new SimpleDateFormat(formatoHoras);
		
		//paso el t a minutos ya que el str es sumatoria de minutos
		int tHoras = Integer.parseInt(dateFormatHours.format(t));
		int tMinutos = Integer.parseInt(dateFormatMinutes.format(t)) + (tHoras*60);
		
		int tcbHoras = Integer.parseInt(dateFormatHours.format(tcb[indiceMenorTcb]));
		int tcbMinutos = Integer.parseInt(dateFormatMinutes.format(tcb[indiceMenorTcb])) + (tcbHoras*60); 
		
		str += (tcbMinutos - (tV/2) - tMinutos);
	}

	private static void calcularStrConTcm() {
		String formatoHoras="HH";
		String formatoMinutos="mm";
		SimpleDateFormat dateFormatMinutes = new SimpleDateFormat(formatoMinutos);
		SimpleDateFormat dateFormatHours = new SimpleDateFormat(formatoHoras);
		
		//paso el t a minutos ya que el str es sumatoria de minutos
		int tHoras = Integer.parseInt(dateFormatHours.format(t));
		int tMinutos = Integer.parseInt(dateFormatMinutes.format(t)) + (tHoras*60);
		
		int tcmHoras = Integer.parseInt(dateFormatHours.format(tcm[indiceMenorTcm]));
		int tcmMinutos = Integer.parseInt(dateFormatMinutes.format(tcm[indiceMenorTcm])) + (tcmHoras*60); 
		
		str += (tcmMinutos - (tV/2) - tMinutos);
	}

	private static void mostrarResultados() {
		
		String formatoHoras="HH";
		String formatoMinutos="mm";
		SimpleDateFormat dateFormatMinutes = new SimpleDateFormat(formatoMinutos);
		SimpleDateFormat dateFormatHours = new SimpleDateFormat(formatoHoras);
		
		int tfHoras = Integer.parseInt(dateFormatHours.format(tf));
		int tfMinutos = Integer.parseInt(dateFormatMinutes.format(tf)) + (tfHoras*60);
		
		int tiMinutos = 1200; //20hs x 60min = 1200min
		
		int stomMinutos, stobMinutos;
		float[] ptom = new float[tcm.length];
		float[] ptob = new float[tcb.length];
		
		for (int i=0; i<tcm.length; i++) {
			stomMinutos = (Integer.parseInt(dateFormatHours.format(stom[i]))*60) + Integer.parseInt(dateFormatMinutes.format(stom[i]));
			ptom[i] = (float) stomMinutos / (float) (tfMinutos - tiMinutos);
		}
		
		for (int i=0; i<tcb.length; i++) {
			stobMinutos = (Integer.parseInt(dateFormatHours.format(stob[i]))*60) + Integer.parseInt(dateFormatMinutes.format(stob[i]));
			ptob[i] = (float) stobMinutos / (float) (tfMinutos - tiMinutos);
		}
		
		float ptr = str / nt;
		
		DecimalFormat decimales = new DecimalFormat("0.00");
		
		System.out.println("M: " + mCantMotos);
		System.out.println("B: " + bCantBicicletas);
		System.out.println("PTR: " + decimales.format(ptr));

		for (int i=0; i<ptob.length; i++) {
			System.out.println("PTOB " + (i+1) + "/" + ptob.length + " :" + decimales.format(ptob[i]));	
		}
		
		for (int i=0; i<ptom.length; i++) {
			System.out.println("PTOM " + (i+1) + "/" + ptom.length + " :" + decimales.format(ptom[i]));	
		}
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

	private static int getMenorTcx(Date[] arrayTcx) {
		Date menor = arrayTcx[0];
		int indiceMenorTcx = 0;
		for (int i=0; i<arrayTcx.length; i++) {
			if (arrayTcx[i].before(menor)) {
				menor = arrayTcx[i];
				indiceMenorTcx = i;
			}
		}
		return indiceMenorTcx;
	}

	private static void setTipoPedidoYTc(float r) {
		if (r<= 0.25) {
			p = 1;
			tc = 20;
		} else if (r <= 0.60) {
			p = 1;
			tc = 15;
		} else {
			p = 2;
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
		stom = new Date[mCantMotos-1];
		
		System.out.print("Ingrese cantidad de bicicletas: ");
		bCantBicicletas = sc.nextInt();	//B
		
		tcb = new Date[bCantBicicletas-1];
		stob = new Date[bCantBicicletas-1];
		
		//asigno variables
		SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm:ss");
		t = formatoFecha.parse(HORA_INICIAL);
		tf = formatoFecha.parse(HORA_FINAL);
		tpll = t;
		str = 0;
        
		for (int i=0; i<tcc.length; i++) {
			tcc[i] = formatoFecha.parse(HORA_INICIAL);
		}
		
		for (int i=0; i<tcm.length; i++) {
			tcm[i] = formatoFecha.parse(HORA_INICIAL_TCM_TCB);
		}

		for (int i=0; i<tcb.length; i++) {
			tcb[i] = formatoFecha.parse(HORA_INICIAL_TCM_TCB);
		}
		
		for (int i=0; i<stom.length; i++) {
			stom[i] = formatoFecha.parse(HORA_CERO);
		}
		
		for (int i=0; i<stob.length; i++) {
			stob[i] = formatoFecha.parse(HORA_CERO);
		}

		
		sc.close();
	}

	private static int getIntervaloArribo() {
		// TODO Auto-generated method stub
		//retorna el iA en minutos
		return 15;	//FIXME hardcodeo
	}
}
