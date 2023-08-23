package teste;

import modelo.Profissional;
import modelo.SuperTabela;
import modelo.Amador;

public class TesteTabels {
	public static void main(String[] args) {
		Amador a = new Amador();
		a.setId(10);
		a.setNome("coisa");
		printTableData(a);
		a.setPk(20);
		printTableData(a);
		Amador a2 = new Amador();
		a2.setId(500);
		printTableData(a2);
		Profissional p = new Profissional();
		p.setCpf(11122233345L);
		p.setNome("Profissional da alegria");
		printTableData(p);
		p.setCpf(99922233345L);
		printTableData(p);
	}

	private static void printTableData(SuperTabela tab) {
		System.out.println("Classe:"+tab.getClass().getName());
		System.out.println("Table Name:"+tab.getTableName());
		System.out.println("Tabela pknome:"+ tab.getPkName());
		System.out.println("Valor PK:"+ tab.getPk());
		System.out.println("Campos obrigatórios preenchidos:"+ tab.isCamposObrigatoriosPreenchidos());
		System.out.println("------------------------------");
	}
}
