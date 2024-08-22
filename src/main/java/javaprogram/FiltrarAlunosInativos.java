package javaprogram;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class Aluno {
    String nome;
    LocalDate dataNascimento;
    String nomeMae;
    String status;
    String assinatura;

    Aluno(String nome, LocalDate dataNascimento, String nomeMae, String status, String assinatura) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.nomeMae = nomeMae;
        this.status = status;
        this.assinatura = assinatura;
    }

    boolean isMaiorDeIdade() {
        return Period.between(this.dataNascimento, LocalDate.now()).getYears() >= 18;
    }

    @Override
    public String toString() {
        return "Nome: " + nome + ", Data de Nascimento: " + dataNascimento +
                ", Nome da Mãe: " + nomeMae + ", Status: " + status +
                ", Assinatura: " + assinatura;
    }
}

public class FiltrarAlunosInativos {
    public static void main(String[] args) {
        String caminhoArquivoExcel = "src/alunos.xlsx";
        List<Aluno> alunosInativosSemAssinatura = new ArrayList<>();
        List<Aluno> alunosInativosSemAssinaturaMaiorIdade = new ArrayList<>();

        try (FileInputStream file = new FileInputStream(caminhoArquivoExcel);
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Pula o cabeçalho
                }

                Cell cellNome = row.getCell(0);
                Cell cellDataNascimento = row.getCell(1);
                Cell cellNomeMae = row.getCell(2);
                Cell cellStatus = row.getCell(3);
                Cell cellAssinatura = row.getCell(4);

                String nome = (cellNome != null) ? cellNome.getStringCellValue() : "";
                LocalDate dataNascimento = null;
                if (cellDataNascimento != null) {
                    if (cellDataNascimento.getCellType() == CellType.NUMERIC) {
                        dataNascimento = cellDataNascimento.getLocalDateTimeCellValue().toLocalDate();
                    } else if (cellDataNascimento.getCellType() == CellType.STRING) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        dataNascimento = LocalDate.parse(cellDataNascimento.getStringCellValue(), formatter);
                    }
                }
                String nomeMae = (cellNomeMae != null) ? cellNomeMae.getStringCellValue() : "";
                String status = (cellStatus != null) ? cellStatus.getStringCellValue() : "";
                String assinatura = (cellAssinatura != null) ? cellAssinatura.getStringCellValue() : "";

                Aluno aluno = new Aluno(nome, dataNascimento, nomeMae, status, assinatura);

                if (status.equalsIgnoreCase("inativo") && assinatura.equalsIgnoreCase("não")) {
                    alunosInativosSemAssinatura.add(aluno);
                    if (aluno.isMaiorDeIdade()) {
                        alunosInativosSemAssinaturaMaiorIdade.add(aluno);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Alunos com status inativo e que não possuem assinatura:");
        for (Aluno aluno : alunosInativosSemAssinatura) {
            System.out.println(aluno);
        }

        System.out.println("\nAlunos com status inativo, que não possuem assinatura e são maior de idade:");
        for (Aluno aluno : alunosInativosSemAssinaturaMaiorIdade) {
            System.out.println(aluno);
        }
    }
}