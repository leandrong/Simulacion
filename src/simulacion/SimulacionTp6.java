package simulacion;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class SimulacionTp6 {
	
	private static final int _0HS = 0;
	private static final int _20_15HS = 1215;
	private static final int _20HS = 1200;
	private static final int _24HS = 1440;
	static int mCantMotos; //M
	static int bCantBicicletas; //B
	static int indiceMenorTcc, indiceMenorTcm, indiceMenorTcb;
	static int t, tf, tpll, menorTcm;
	static int sto = 0;
	static int nt = 0;
	static int str = 0;
	static int[] tcc, tcm, tcb, stom, stob;
	static int p; //tipo de pedido
	static int tc; //tiempo comprometido
	static int iA;
	static int tV;
	
	public static void main(String arg[]){
		
		float r; //random
		tcc = new int[8];
		
//		Date t, tf, tpll, menorTcm;
//		Date[] tcc, tcm, tcb, stom, stob;
//		tcc = new Date[8];	//tiempo comprometido cocina
		
		try {
			condicionesIniciales();
			
			do {
				t = tpll;
				iA = getIntervaloArribo();
				tpll = t + iA;
				r = getRandom();
	
				setTipoPedidoYTc(r);
				
				indiceMenorTcc = getMenorTcx(tcc);
				
				if (t<tcc[indiceMenorTcc]) {	
					//lado NO del diagrama con condicion t >= tcc(y)
					tcc[indiceMenorTcc] += tc;
				} else {
					//lado SI del diagrama con condicion t >= tcc(y)
					tcc[indiceMenorTcc] = t + tc;
				}
				
				tV = getTiempoViaje();
				
				//k = getRandom();
				
				nt += 1;
				
				if (tV >10 || p==2) {
					//moto
					indiceMenorTcm = getMenorTcx(tcm);
					if (tcc[indiceMenorTcc] < tcm[indiceMenorTcm]) {
						//lado NO
						tcm[indiceMenorTcm] += tV;
					} else {
						//lado SI
						stom[indiceMenorTcm] += tcc[indiceMenorTcc] - tcm[indiceMenorTcm];
						tcm[indiceMenorTcm] = tcc[indiceMenorTcc] + tV;
					}
					calcularStrConTcm();
				} else {
					//bici o moto
					indiceMenorTcb = getMenorTcx(tcb);
					indiceMenorTcm = getMenorTcx(tcm);
					if (tcb[indiceMenorTcb] > tcm[indiceMenorTcm]) {
						//lado NO
						if (tcc[indiceMenorTcc] < tcm[indiceMenorTcm]) {
							//lado NO
							tcm[indiceMenorTcm] += tV;
						} else {
							//lado SI
							stom[indiceMenorTcm] += tcc[indiceMenorTcc] - tcm[indiceMenorTcm];
							tcm[indiceMenorTcm] = tcc[indiceMenorTcc] + tV;
						}
						calcularStrConTcm();
					} else {
						//lado SI
						if (tcc[indiceMenorTcc] < tcb[indiceMenorTcb]) {
							//lado NO
							tcb[indiceMenorTcb] += tV;
						} else {
							//lado SI
							stob[indiceMenorTcb] += tcc[indiceMenorTcc] - tcb[indiceMenorTcb];
							tcb[indiceMenorTcb] = tcc[indiceMenorTcc] + tV;
						}
						calcularStrConTcb();
					}
				}
				
			} while (t < tf);
			
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
			if (tcm[i] < tf) {
				stom[i] += tf - tcm[i];
			}
		}
		
		for(int i=0; i<tcb.length; i++) {
			if (tcb[i] < tf) {
				stob[i] += tf - tcb[i];
			}
		}
	}

	private static void calcularStrConTcb() {
		str += (tcb[indiceMenorTcb] - (tV/2) - t);
	}

	private static void calcularStrConTcm() {
		str += (tcm[indiceMenorTcm] - (tV/2) - t);
	}

	private static void mostrarResultados() {
		
		int tiMinutos = _20HS; //20hs x 60min = 1200min
		
		float[] ptom = new float[tcm.length];
		float[] ptob = new float[tcb.length];
		
		for (int i=0; i<tcm.length; i++) {
			ptom[i] = (float) stom[i] / (float) (tf - tiMinutos);
		}
		
		for (int i=0; i<tcb.length; i++) {
			ptob[i] = (float) stob[i] / (float) (tf - tiMinutos);
		}
		
		float ptr = (float) str / (float) nt;
		
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

	private static int getTiempoViaje() {
		float r1, r2, x, y;
		double fx;
		
		do {
			r1 = getRandom();
			r2 = getRandom();
			
			x = 19*r1 + 3;
			y = (float) (r2*0.08);
			
			fx = 1.97*(Math.pow((x/16.5), 0.97)) / 16.5*Math.pow((1+(Math.pow((x/16.5),7.31))),1.27);
			
		} while (fx<y);
		
		
		return (int)x;
	}

	private static int getMenorTcx(int[] arrayTcx) {
		int menor = arrayTcx[0];
		int indiceMenorTcx = 0;
		for (int i=0; i<arrayTcx.length; i++) {
			if (arrayTcx[i] < menor) {
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

	public static void condicionesIniciales() throws ParseException {
		
		System.out.print("Ingrese cantidad de motos: ");
		Scanner sc = new Scanner(System.in);
		mCantMotos = sc.nextInt();	//M
		
		tcm = new int[mCantMotos-1];
		stom = new int[mCantMotos-1];
		
		System.out.print("Ingrese cantidad de bicicletas: ");
		bCantBicicletas = sc.nextInt();	//B
		
		tcb = new int[bCantBicicletas-1];
		stob = new int[bCantBicicletas-1];
		
		//asigno variables
		//SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm:ss");
		t = _20HS;
		tf = _24HS;
		tpll = t;
		str = 0;
        
		for (int i=0; i<tcc.length; i++) {
			tcc[i] = _20HS;
		}
		
		for (int i=0; i<tcm.length; i++) {
			tcm[i] = _20_15HS;
		}

		for (int i=0; i<tcb.length; i++) {
			tcb[i] = _20_15HS;
		}
		
		for (int i=0; i<stom.length; i++) {
			stom[i] = _0HS;
		}
		
		for (int i=0; i<stob.length; i++) {
			stob[i] = _0HS;
		}

		
		sc.close();
	}

	private static int getIntervaloArribo() {
		//retorna el iA en minutos
		float r1, r2, x, y;
		double fx;
		
		do {
			r1 = getRandom();
			r2 = getRandom();
			
			x = 32*r1 + 1;
			y = (float) (r2*0.15);
			
			fx = 0.12*(Math.pow(x, 0.86)) / Math.pow((1+0.48*(Math.pow(x,1.86))),2.26);
			
		} while (fx<y);
		
		
		return (int)x;
	}
	

	private static int abs (int numero) {
	      return numero > 0 ? numero : -numero;
	}
}
