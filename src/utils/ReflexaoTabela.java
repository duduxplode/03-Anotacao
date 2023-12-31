package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import anotacao.Campo;
import anotacao.Tabela;
import modelo.SuperTabela;

public class ReflexaoTabela {
	private static Field getPkField(SuperTabela tab) {
		validarParametroTab(tab);
		int numPks = 0;
		Field pkField = null;
		String pkNome = null;
		// obter metadado do objeto SuperTabela
		Class<?> cls = tab.getClass();
		// obter nome dos atributos da classe
		Field[] atributos = cls.getDeclaredFields();

		// percorrer os atributos procurando pela anotacao @Campo
		for (Field attr : atributos) {
			if (attr.isAnnotationPresent(Campo.class)) {
				Campo cmp = attr.getAnnotation(Campo.class);
				if (cmp.isPk()) {
					pkField = attr;
					pkNome = cmp.colunaNome();
					numPks++;
				}
			}
		}
		if (pkField == null || pkNome.isEmpty()) {
			throw new RuntimeException(
					"Classe: " + cls.getName() + " n�o tem nenhum atributo anotado com @Campo(isPk=True)");
		} else if (numPks > 1) {
			throw new RuntimeException(
					"Classe: " + cls.getName() + " tem mais de um atributo anotado com @Campo(isPk=True)");
		}
		return pkField;
	}
	public static String getPkName(SuperTabela tab) {
		Field pkField = getPkField(tab);
		Campo cmp = pkField.getAnnotation(Campo.class);
		return cmp.colunaNome();

	}
	public static Object getPkValue(SuperTabela tab) {
		Field pkField = getPkField(tab);
		String pkMethodName = "get"+getUCFirst(pkField.getName());
		return invokeGetMethod(tab, pkMethodName);
	}

	public static String getTableName(SuperTabela tab) {
		Class<?> cls = tab.getClass();
		String ret = null;
		if (cls.isAnnotationPresent(Tabela.class)) {
			Tabela tabela = cls.getAnnotation(Tabela.class);
			ret = tabela.tabelaNome();
		}
		return ret;
	}

	private static void validarParametroTab(SuperTabela tab) {
		if (tab == null) {
			throw new RuntimeException("Erro Metodo ReflexaoTabela.getPkNome, deve receber um objeto n�o nulo");
		}
	}

	private static Object invokeGetMethod(SuperTabela tab, String pkMethodName) {
		return invokeMethod(tab, pkMethodName, null);
	}
	private static Object invokeMethod(SuperTabela tab, String pkMethodName,Object value) {
		Class<?> cls = tab.getClass();
		
		Method pkMethod = null;
		try {
			if(value==null) {
				pkMethod = cls.getMethod(pkMethodName);
				return pkMethod.invoke(tab);
			}else {
				pkMethod = cls.getMethod(pkMethodName, value.getClass());
				return pkMethod.invoke(tab,value);
			}
			
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("A classe: "+cls.getName()+" n�o possui o metodo: "+pkMethodName+"("+value.getClass().getName()+") com visibilidade p�blica, ou n�o existe!");
		} catch (IllegalAccessException|
				IllegalArgumentException|
				InvocationTargetException e) {
			throw new RuntimeException("Houve um erro desconhecido na execu��o do m�todo:"+cls.getName()+"."+pkMethodName,e);
		}
	}
	public static String getUCFirst(String name) {
		return name.substring(0,1).toUpperCase()+name.substring(1);
	}
	public static void setPkValue(SuperTabela<?> tab, Object value) {
		Field pkField = getPkField(tab);
		setValue(tab,pkField, value);
	}
	public static void setValue(SuperTabela<?> tab, Field field, Object value) {
		String pkMethodName = "set"+getUCFirst(field.getName());
		invokeMethod(tab,pkMethodName,value);
	}

	public static Boolean validarCamposObrigatorios(SuperTabela tab) {
		// obter metadado do objeto SuperTabela
		Class<?> cls = tab.getClass();
		// obter nome dos atributos da classe
		Field[] atributos = cls.getDeclaredFields();
		// percorrer os atributos procurando pela anotacao @Campo
		for (Field attr : atributos) {
			if (attr.isAnnotationPresent(Campo.class)) {
				Campo cmp = attr.getAnnotation(Campo.class);
				if (cmp.isObrigatorio()) {
					String pkMethodName = "get"+getUCFirst(attr.getName());
					Object obj = invokeGetMethod(tab, pkMethodName);
					if (Objects.isNull(obj) || obj.toString().isEmpty())
						return false;
				}
			}
		}
		return true;
	}
}
