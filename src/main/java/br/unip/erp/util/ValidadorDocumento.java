package br.unip.erp.util;

/**
 * Validacao de CPF e CNPJ pelo digito verificador (algoritmo oficial).
 */
public final class ValidadorDocumento {

    private ValidadorDocumento() { }

    /** Remove qualquer caractere que nao seja digito. */
    private static String digitos(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    /**
     * Valida um documento (CPF com 11 digitos ou CNPJ com 14 digitos).
     * @return true se o documento for valido pelo digito verificador
     */
    public static boolean isValido(String documento) {
        String d = digitos(documento);
        if (d.length() == 11) {
            return isCpfValido(d);
        }
        if (d.length() == 14) {
            return isCnpjValido(d);
        }
        return false;
    }

    /** Valida CPF (11 digitos). */
    public static boolean isCpfValido(String cpf) {
        String d = digitos(cpf);
        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            return false; // tamanho invalido ou todos os digitos iguais
        }
        try {
            int dig1 = calcularDigitoCpf(d, 9, 10);
            int dig2 = calcularDigitoCpf(d, 10, 11);
            return dig1 == (d.charAt(9) - '0') && dig2 == (d.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private static int calcularDigitoCpf(String d, int qtdDigitos, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < qtdDigitos; i++) {
            soma += (d.charAt(i) - '0') * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    /** Valida CNPJ (14 digitos). */
    public static boolean isCnpjValido(String cnpj) {
        String d = digitos(cnpj);
        if (d.length() != 14 || d.chars().distinct().count() == 1) {
            return false;
        }
        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int dig1 = calcularDigitoCnpj(d, pesos1);
            int dig2 = calcularDigitoCnpj(d, pesos2);
            return dig1 == (d.charAt(12) - '0') && dig2 == (d.charAt(13) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private static int calcularDigitoCnpj(String d, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (d.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}
