package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Main {

	public static double getDistance(Point p1, Point p2) {
		int x1 = p1.getX();
		int x2 = p2.getX();
		int y1 = p1.getY();
		int y2 = p2.getY();
		return Math.hypot(x1-x2, y1-y2);
	}
	
	/**
	 * Retorna true se o cachorro pode sair de p1 para p2, antes de ir para p3
	 * @param p1 ponto de partida em que o dono e o cachorro estão
	 * @param p2 um ponto interessante para o cachorro
	 * @param p3 proximo ponto em que o dono estará
	 * @return true se o cachorro pode desviar, false cc
	 */
	public static boolean podeDesviar(Point p1, Point p2, Point p3) {
		double distanciaP1P2 = getDistance(p1, p2);
		double distanciaP2P3 = getDistance(p2, p3);
		double distanciaP1P3 = getDistance(p1,p3);
		return distanciaP1P2 + distanciaP2P3 <= 2 * distanciaP1P3;
	}
	
	public class Point {
		int x;
		int y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
		
		public String toString() {
			return "(" + this.x + "," + this.y + ")";
		}
	}
	
	public class Vertice {
		
		public static final int BRANCO = 0;
		public static final int CINZA = 1;
		public static final int PRETO = 2;
		
		int numeroVertice;
		private Point ponto;
		private List<Vertice> adjacentes;

		private Vertice pai;
		private int cor;

		public Vertice(int numeroVertice) {
			this.numeroVertice = numeroVertice;
			this.adjacentes = new LinkedList<>();
			this.cor = BRANCO;
		}

		public int getNumeroVertice() {
			return numeroVertice;
		}

		public List<Vertice> getAdjacentes() {
			return adjacentes;
		}
		
		public Point getPonto() {
			return ponto;
		}
		
		public void setPonto(Point ponto) {
			this.ponto = ponto;
		}

		public void adicionarAresta(Vertice vertice) {
			if(!this.adjacentes.contains(vertice)) {
				this.adjacentes.add(vertice);
			}
		}
		
		public void removerAresta(Vertice vertice) {
			this.adjacentes.remove(vertice);
		}

		public Vertice getPai() {
			return pai;
		}

		public void setPai(Vertice pai) {
			this.pai = pai;
		}
		
		public int getCor() {
			return this.cor;
		}

		public void setCor(int cor) {
			this.cor = cor;
		}

	}

	public class Grafo {
		private List<Vertice> vertices;
		private int[][] c;
		private int[][] f;
		private int pontosDoDono;
		private int pontosInteressantes;
		
		public Grafo(int pontosDoDono, int pontosInteressantes) {
			this.vertices = new ArrayList<>();
			this.pontosDoDono = pontosDoDono;
			this.pontosInteressantes = pontosInteressantes;
			//+ 2 porque temos o source e o sink
			int tamanhoMatriz = this.pontosDoDono + this.pontosInteressantes + 2;
			c = new int[tamanhoMatriz][tamanhoMatriz];
			f = new int[tamanhoMatriz][tamanhoMatriz];
			for (int i = 0; i < tamanhoMatriz; i++) {
				//Aqui ele ja carrega todos os vertices, inclusive o source e o sink
				this.vertices.add(new Vertice(i));
				for (int j = 0; j < tamanhoMatriz; j++) {
					c[i][j] = 0;
					f[i][j] = 0;
				}
			}
		}

		public List<Vertice> getVertices() {
			return vertices;
		}

		public void setVertices(List<Vertice> vertices) {
			this.vertices = vertices;
		}

		public int getCapacidadeAresta(Vertice origem, Vertice destino) {
			return c[origem.getNumeroVertice()][destino.getNumeroVertice()];
		}

		public void inicializarVertices() {
			for (Vertice vertice : this.vertices) {
				vertice.setPai(null);
				vertice.setCor(Vertice.BRANCO);
			}
		}
		
		//Para cada ponto do caminho do dono, verifica se algum ponto interessante eh alcancavel
		//pelo cachorro
		public void criarArestasGrafoBipartido() {
			for (int i = 0; i < this.pontosDoDono - 1; i++) {
				for (int j = this.pontosDoDono; j < this.pontosDoDono + this.pontosInteressantes; j++) {
					Vertice pontoDonoOrigem = this.vertices.get(i);
					Vertice pontoInteressante = this.vertices.get(j);
					Vertice pontoDonoDestino = this.vertices.get(i+1);
					
					Point p1 = pontoDonoOrigem.getPonto();
					Point p2 = pontoInteressante.getPonto();
					Point p3 = pontoDonoDestino.getPonto();
					if(podeDesviar(p1, p2, p3)) {
						adicionarAresta(i, j, 1);
					}
				}
			}
			adicionarArestasDoSourceEDoSink();
		}

		private void adicionarArestasDoSourceEDoSink() {
			//Resolvendo problema de multiplos sources e multiplos sinks
			int indiceSorce = this.vertices.size() - 2;
			int indiceSink = this.vertices.size() - 1;
			for (int i = 0; i < this.pontosDoDono; i++) {
				adicionarAresta(indiceSorce, i, 1);
			}
			for (int i = this.pontosDoDono; i < this.pontosDoDono + this.pontosInteressantes; i++) {
				adicionarAresta(i, indiceSink, 1);
			}
		}
		
		public void adicionarAresta(int origem, int destino, int capacidade) {
			Vertice verticeOrigem = this.vertices.get(origem);
			Vertice verticeDestino = this.vertices.get(destino);
			verticeOrigem.adicionarAresta(verticeDestino);
			c[origem][destino] = capacidade;
		}
		
		public void removerAresta(int origem, int destino) {
			Vertice verticeOrigem = this.vertices.get(origem);
			Vertice verticeDestino = this.vertices.get(destino);
			verticeOrigem.removerAresta(verticeDestino);
		}
		
		public boolean existeCaminhoAumentante(Vertice origem, Vertice destino) {
			inicializarVertices();
			//Lista de vertices visitados
			Queue<Vertice> filaVertices = new LinkedList<Vertice>();

			//Raiz ja foi visitada, logo fica com a cor cinza
			origem.setCor(Vertice.CINZA);
			filaVertices.add(origem);
			Vertice verticeAtual;
			List<Vertice> adjacentes;
			while(!filaVertices.isEmpty()) {
				verticeAtual = filaVertices.remove();
				adjacentes = verticeAtual.getAdjacentes();
				for (Vertice adj: adjacentes) {
					if (adj.getCor() == Vertice.BRANCO) {
						adj.setCor(Vertice.CINZA);
						adj.setPai(verticeAtual);
						//Se eu cheguei no destino, quer dizer q existe caminho aumentante
						if(adj.equals(destino)) {
							return true;
						}
						filaVertices.add(adj);
					}
				}
				verticeAtual.setCor(Vertice.PRETO);
			}
			return false;
		}
		
		public int coletarGargaloDoCaminho(Vertice origem, Vertice destino) {
			int gargalo = Integer.MAX_VALUE;
			Vertice atual = destino, anterior;
			while(true) {
				anterior = atual.getPai();
				if(c[anterior.getNumeroVertice()][atual.getNumeroVertice()] < gargalo) {
					gargalo = c[anterior.getNumeroVertice()][atual.getNumeroVertice()];
				}
				//Se chegou na origem, ja acabou de percorrer o caminho
				if(anterior.equals(origem)) {
					break;
				}
				atual = anterior;
			}
			//Se existe caminho eh pq da pra passar fluxo, entao o gargalo eh maior q zero
			assert(gargalo > 0);
			return gargalo;
		}
		
		public void aumentarFluxo(Vertice origem, Vertice destino, int qtdeFluxo) {
			Vertice atual = destino, anterior;
			while(true) {
				anterior = atual.getPai();
				f[anterior.getNumeroVertice()][atual.getNumeroVertice()] += qtdeFluxo;
				//cf(u,v) = c(u,v) - f(u,v)
				c[anterior.getNumeroVertice()][atual.getNumeroVertice()] -= qtdeFluxo;
				
				//f(u,v) = -f(v,u)
				f[atual.getNumeroVertice()][anterior.getNumeroVertice()] = - f[anterior.getNumeroVertice()][atual.getNumeroVertice()];

				//Se usou o maximo da banda naquela aresta, deve ser eliminada do grafo
				if(c[anterior.getNumeroVertice()][atual.getNumeroVertice()] == 0) {
					anterior.removerAresta(atual);
					//E a aresta no sentido contraria deve ser criada, como segue a regra do grafo residual
					atual.adicionarAresta(anterior);
					c[atual.getNumeroVertice()][anterior.getNumeroVertice()] = qtdeFluxo;
				}
				else {
					c[atual.getNumeroVertice()][anterior.getNumeroVertice()] += qtdeFluxo;
				}
				
				//Se chegou na origem, ja acabou de percorrer o caminho
				if(anterior.equals(origem)) {
					break;
				}
				atual = anterior;
			}
		}
		
		public void rodarFluxoMaximo() {
			//Source
			int verticeOrigem = this.vertices.size() - 2;
			//Sink
			int verticeDestino = this.vertices.size() - 1;
			Vertice origem = this.vertices.get(verticeOrigem);
			Vertice destino = this.vertices.get(verticeDestino);
			int gargalo;
			while(existeCaminhoAumentante(origem, destino)) {
				gargalo = coletarGargaloDoCaminho(origem, destino);
				aumentarFluxo(origem, destino, gargalo);
			}
		}
		
		public List<Point> coletarRotaDoCachorro(){
			List<Point> rotaDoCachorro = new LinkedList<>();
			for (int i = 0; i < this.pontosDoDono; i++) {
				rotaDoCachorro.add(this.vertices.get(i).getPonto());
				for (int j = this.pontosDoDono; j < this.pontosDoDono + this.pontosInteressantes; j++) {
					//Se ta passando fluxo, eh pq o cachorro pode fazer esse desvio
					if(f[i][j] == 1) {
						rotaDoCachorro.add(this.vertices.get(j).getPonto());
					}
				}
			}
			return rotaDoCachorro;
		}
	}

	public static void main(String[] args) {
		Main mainObject = new Main();
		Scanner scanner = new Scanner(System.in);
		int numeroGrafos = scanner.nextInt();
		int numeroPontosDono, numeroPontosInteressantes;
		int cordX, cordY;
		while (numeroGrafos-- > 0) {
			numeroPontosDono = scanner.nextInt();
			numeroPontosInteressantes = scanner.nextInt();
			Grafo grafo = mainObject.new Grafo(numeroPontosDono,numeroPontosInteressantes);

			//Inserindo os pontos nos vertices
			for (int i = 0; i < numeroPontosDono + numeroPontosInteressantes; i++) {
				cordX = scanner.nextInt();
				cordY = scanner.nextInt();
				grafo.getVertices().get(i).setPonto(mainObject.new Point(cordX,cordY));
			}
			grafo.criarArestasGrafoBipartido();
			grafo.rodarFluxoMaximo();
			List<Point> rotaDoCachorro = grafo.coletarRotaDoCachorro();
			System.out.println(rotaDoCachorro.size());
			
			for (int i = 0; i < rotaDoCachorro.size() - 1; i++) {
				Point point = rotaDoCachorro.get(i);
				System.out.print(point.getX() + " " + point.getY() + " ");
			}
			Point ultimoPonto = rotaDoCachorro.get(rotaDoCachorro.size()-1);
			System.out.println(ultimoPonto.getX() + " " + ultimoPonto.getY());
			System.out.println();
			scanner.nextLine();
		}
	}
}
