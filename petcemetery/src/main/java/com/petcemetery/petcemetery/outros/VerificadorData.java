package com.petcemetery.petcemetery.outros;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.petcemetery.petcemetery.model.Jazigo;
import com.petcemetery.petcemetery.model.Lembrete;
import com.petcemetery.petcemetery.model.Pagamento;
import com.petcemetery.petcemetery.model.Reuniao;
import com.petcemetery.petcemetery.model.HistoricoServicos;
import com.petcemetery.petcemetery.model.Jazigo.StatusEnum;
import com.petcemetery.petcemetery.model.Servico.ServicoEnum;
import com.petcemetery.petcemetery.repositorio.JazigoRepository;
import com.petcemetery.petcemetery.repositorio.LembreteRepository;
import com.petcemetery.petcemetery.repositorio.PagamentoRepository;
import com.petcemetery.petcemetery.repositorio.ReuniaoRepository;
import com.petcemetery.petcemetery.repositorio.HistoricoServicosRepository;
import com.petcemetery.petcemetery.services.EmailService;

import java.util.List;

@Component
public class VerificadorData {

    @Autowired
    private EmailService emailService;

    @Autowired
    private HistoricoServicosRepository historicoServicosRepository;

    @Autowired
    private JazigoRepository jazigoRepository;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private LembreteRepository lembreteRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    private static LocalDate currentDate = LocalDate.now();

    public static LocalDate getCurrentDate() {
        return currentDate;
    }

    public static void setCurrentDate(LocalDate date) {
        currentDate = date;
    }

    public void avancarData(LocalDate novaData) {
        setCurrentDate(novaData);
    }
    
    // Checa a cada 2 minutos se há algum serviço de enterro para ser realizado, e envia um email pro cliente caso o enterro do pet dele seja hoje
    @Scheduled(cron = "*/30 * * * * ?") // Executa a cada dois minutos
    public void checaEnterros() {
        LocalDate dataAtual = VerificadorData.getCurrentDate();
        List<HistoricoServicos> historicoServicos = historicoServicosRepository.findAll();

        for (HistoricoServicos servico : historicoServicos) {
            if(servico.getTipoServico() != ServicoEnum.ENTERRO) {
                continue;
            }

            LocalDate dataEnterro = servico.getDataServico();

            if (dataEnterro.equals(dataAtual) && servico.getJazigo().getPetEnterrado() == null) {
                Jazigo jazigo = servico.getJazigo();
                jazigo.setPetEnterrado(servico.getPet());
                if(jazigo.getHistoricoPets().contains(servico.getPet())) {
                    continue;
                }
                jazigo.getHistoricoPets().add(servico.getPet());
                jazigoRepository.save(jazigo);

                // Enviar o e-mail de aviso
                String[] email = {servico.getCliente().getEmail()};
                String assunto = "Lembrete de Enterro";
                String mensagem = "Lembrete: Hoje é o dia do enterro do seu pet no cemitério.";
                emailService.sendEmail(email, assunto, mensagem);
            }
        }
    }

    // Checa a cada 2 minutos se há algum serviço de exumação para ser realizado, e envia um email pro cliente caso a cremação do pet dele seja hoje
    @Scheduled(cron = "*/30 * * * * ?") // Executa a cada dois minutos
    public void checaExumacoes() {
        LocalDate dataAtual = VerificadorData.getCurrentDate();
        System.out.println(dataAtual.toString());
        List<HistoricoServicos> historicoServicos = historicoServicosRepository.findAll();


        for (HistoricoServicos servico : historicoServicos) {
            if(servico.getTipoServico() != ServicoEnum.EXUMACAO) {
                continue;
            }
            LocalDate dataExumacao = servico.getDataServico();

            if (dataExumacao.equals(dataAtual) && servico.getJazigo().getPetEnterrado() != null) {
                Jazigo jazigo = servico.getJazigo();
                jazigo.setPetEnterrado(null);
                jazigo.setStatus(StatusEnum.DISPONIVEL);
                jazigoRepository.save(jazigo);

                // Enviar o e-mail de aviso
                String[] email = {servico.getCliente().getEmail()};
                String assunto = "Lembrete de Exumação";
                String mensagem = "Lembrete: Hoje é o dia da exumação do seu pet no cemitério.";
                emailService.sendEmail(email, assunto, mensagem);
            }
        }
    }


    // Checa a cada 2 minutos se há alguma reunião para ser realizada, e envia um email pro cliente caso a reunião dele seja hoje
    @Scheduled(cron = "0 */2 * * * ?") // Executa a cada dois minutos
    public void checaReunioes() {
        LocalDate dataAtual = VerificadorData.getCurrentDate();
        List<Reuniao> reunioes = reuniaoRepository.findAll();

        for (Reuniao reuniao : reunioes) {

            if (reuniao.getData().equals(dataAtual)) {
                // Enviar o e-mail de aviso
                String[] email = {reuniao.getCliente().getEmail()};
                String assunto = "Lembrete de Reunião";
                String mensagem = "Lembrete: Hoje é o dia da sua reunião, que irá ocorrer no horário " + reuniao.getHorario() + ", no cemitério.";
                emailService.sendEmail(email, assunto, mensagem);
            }
        }
    }

    // Checa a cada 2 minutos se há algum pagamento pendente de aluguel para os clientes, e envia um email pro cliente caso o pagamento dele esteja atrasado
    //TODO: Aplicar a classe pagamento aqui quando ela for feita. Por enquanto, está sendo utilizada a classe servico.
    @Scheduled(cron = "0 */2 * * * ?") // Executa a cada dois minutos
    public void checaPagamentoAluguel() {
        LocalDate dataAtual = VerificadorData.getCurrentDate();
        List<Pagamento> pagamentos = pagamentoRepository.findAll();

        // Verifica se o serviço é aluguel e se o pagamento está atrasado
        for (Pagamento pagamento : pagamentos) {
            if(pagamento.getServico().getTipoServico() != ServicoEnum.ALUGUEL) {
                continue;
            }

            if (pagamento.getDataVencimento().isBefore(dataAtual)) {
                // Enviar o e-mail de aviso
                String[] email = {pagamento.getCliente().getEmail()};
                String assunto = "Lembrete de Pagamento";
                String mensagem = "Lembrete: O pagamento do serviço de aluguel do seu jazigo está atrasado. Por favor, entre em contato conosco para regularizar a situação.";
                emailService.sendEmail(email, assunto, mensagem);
                pagamento.getCliente().setInadimplente(true);
            }
        }
    }

    // Checa a cada 2 minutos há algum pagamento pendente de manutenção para os clientes, e envia um email pro cliente caso o pagamento dele esteja atrasado
    //TODO: Aplicar a classe pagamento aqui quando ela for feita. Por enquanto, está sendo utilizada a classe servico.
    @Scheduled(cron = "0 */2 * * * ?") // Executa a cada dois minutos
    public void checaPagamentoManutencao() {
        LocalDate dataAtual = VerificadorData.getCurrentDate();
        List<Pagamento> pagamentos = pagamentoRepository.findAll();

        // Verifica se o serviço é manutenção e se o pagamento está atrasado
        for (Pagamento pagamento : pagamentos) {
            if(pagamento.getServico().getTipoServico() != ServicoEnum.MANUTENCAO) {
                continue;
            }

            if (pagamento.getDataVencimento().isBefore(dataAtual)) {
                // Enviar o e-mail de aviso
                String[] email = {pagamento.getCliente().getEmail()};
                String assunto = "Lembrete de Pagamento";
                String mensagem = "Lembrete: O pagamento do serviço de manutenção do seu jazigo está atrasado. Por favor, entre em contato conosco para regularizar a situação.";
                emailService.sendEmail(email, assunto, mensagem);
                pagamento.getCliente().setInadimplente(true);
            }
        }
    }

    // Checa a cada 2 minutos se há algum lembrete de visita para ser enviado, e envia um email pro cliente caso a visita dele seja hoje
    @Scheduled(cron = "0 */2 * * * ?") // Executa a cada dois minutos
    public void checaLembretes() {
        LocalDate dataAtual = VerificadorData.getCurrentDate();
        // Checa todos os lembretes que ainda não foram enviados
        List<Lembrete> lembretes = lembreteRepository.findAllByEnviado(false);

        for (Lembrete lembrete : lembretes) {
            if (lembrete.getData().equals(dataAtual)) {
                // Enviar o e-mail de aviso
                String[] email = {lembrete.getCliente().getEmail()};
                String assunto = "Lembrete de Visita";
                String mensagem = "Lembrete: Hoje é o dia da sua visita no cemitério.";
                emailService.sendEmail(email, assunto, mensagem);
            }
            lembrete.setEnviado(false);
            lembreteRepository.save(lembrete);
        }
    }

    
    @Scheduled(cron = "*/30 * * * * ?") // Executa a cada 30 segundos
    public void checaNotificacaoRenovacao() {
        LocalDate dataAtual = VerificadorData.getCurrentDate();
        List<HistoricoServicos> historicoServicos = historicoServicosRepository.findAll();

        LocalDate  dataSemanaPassada = dataAtual.minusDays(7);

        // Verifica se o serviço  é aluguel e se a data de renovaçao está em dois dias 
        for (HistoricoServicos servico : historicoServicos) {
            if(servico.getTipoServico() != ServicoEnum.ALUGUEL) {
                continue;
            }
            LocalDate dataServico = servico.getDataServico();

            if (dataServico.plusYears(3).isBefore(dataAtual) && dataServico.plusYears(3).isAfter(dataSemanaPassada)){
                // Enviar o e-mail de notificação
                String[] email = {servico.getCliente().getEmail()};
                String assunto = "Notificação de Renovação de Jazigo";
                String mensagem = "Notificação: O contrato de aluguel do seu Jazigo No PetCemetery está prestes a vencer. Por favor, entre em contato conosco para regularizar a situação.";
                emailService.sendEmail(email, assunto, mensagem);
            }
        }
    }
}