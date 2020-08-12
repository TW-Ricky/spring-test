package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.exception.AmountLessThanMinimumAmount;
import com.thoughtworks.rslist.exception.RsEventNotExistsException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.hibernate.boot.model.source.spi.Sortable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RsService {
  final RsEventRepository rsEventRepository;
  final UserRepository userRepository;
  final VoteRepository voteRepository;
  final TradeRepository tradeRepository;

  public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository, TradeRepository tradeRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.tradeRepository = tradeRepository;
  }

  public void vote(Vote vote, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    Optional<UserDto> userDto = userRepository.findById(vote.getUserId());
    if (!rsEventDto.isPresent()
        || !userDto.isPresent()
        || vote.getVoteNum() > userDto.get().getVoteNum()) {
      throw new RuntimeException();
    }
    VoteDto voteDto =
        VoteDto.builder()
            .localDateTime(vote.getTime())
            .num(vote.getVoteNum())
            .rsEvent(rsEventDto.get())
            .user(userDto.get())
            .build();
    voteRepository.save(voteDto);
    UserDto user = userDto.get();
    user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
    userRepository.save(user);
    RsEventDto rsEvent = rsEventDto.get();
    rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
    rsEventRepository.save(rsEvent);
  }

  public void buy(Trade trade, int id) {
    Optional<RsEventDto> rsEventOptional = rsEventRepository.findById(id);
    if (!rsEventOptional.isPresent()) {
      throw new RsEventNotExistsException("rsEvent not exists");
    }

    Optional<TradeDto> tradeDtoOptional = tradeRepository.findByRank(trade.getRank());

    if (tradeDtoOptional.isPresent()) {
      if (trade.getAmount() <= tradeDtoOptional.get().getAmount()) {
        throw new AmountLessThanMinimumAmount("amount is less than minimum amount");
      }
      if (tradeDtoOptional.get().getRsEventDto().getId() != id) {
        tradeRepository.delete(tradeDtoOptional.get());
        rsEventRepository.delete(tradeDtoOptional.get().getRsEventDto());
      }
    }
    TradeDto tradeDto = TradeDto.builder()
            .amount(trade.getAmount())
            .rank(trade.getRank())
            .build();
    RsEventDto rsEventDto = rsEventOptional.get();
    rsEventDto.setRank(trade.getRank());
    tradeRepository.save(tradeDto);
    rsEventRepository.save(rsEventDto);
  }

  public List<RsEvent> getRsEventList() {
    List<RsEventDto> rsEventDtoList = rsEventRepository.findAll();
    return sortRsEventList(rsEventDtoList);

  }

  private List<RsEvent> sortRsEventList(List<RsEventDto> rsEventDtoList) {
    List<RsEvent> rsEventList = rsEventDtoList.stream()
            .map(item -> changeRsEventDtoToRsEvent(item))
            .sorted(new Comparator<RsEvent>() {
              @Override
              public int compare(RsEvent o1, RsEvent o2) {
                return o2.getVoteNum() - o1.getVoteNum();
              }
            })
            .collect(Collectors.toList());
    List<RsEvent> noBuyRsEventList = rsEventList.stream().filter(item -> item.getRank() == 0).collect(Collectors.toList());
    rsEventList.stream()
            .filter(item -> item.getRank() != 0)
            .sorted(new Comparator<RsEvent>() {
              @Override
              public int compare(RsEvent o1, RsEvent o2) {
                return o1.getRank() - o2.getRank();
              }
            })
            .forEach(item -> noBuyRsEventList.add(item.getRank() - 1, item));
    return noBuyRsEventList;
  }

  private RsEvent changeRsEventDtoToRsEvent(RsEventDto item) {
    return RsEvent.builder().eventName(item.getEventName())
            .userId(item.getId())
            .keyword(item.getKeyword())
            .rank(item.getRank())
            .voteNum(item.getVoteNum())
            .build();
  }
}
