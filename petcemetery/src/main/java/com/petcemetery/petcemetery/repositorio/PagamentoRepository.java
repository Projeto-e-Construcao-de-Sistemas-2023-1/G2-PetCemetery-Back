package com.petcemetery.petcemetery.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petcemetery.petcemetery.model.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Pagamento findByHistoricoServicosIdServico(Long id_servico);
    
}
