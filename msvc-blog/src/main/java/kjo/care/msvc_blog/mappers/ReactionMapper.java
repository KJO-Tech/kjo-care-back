package kjo.care.msvc_blog.mappers;

import jakarta.annotation.PostConstruct;
import kjo.care.msvc_blog.client.UserClient;
import kjo.care.msvc_blog.dto.*;
import kjo.care.msvc_blog.entities.Reaction;
import kjo.care.msvc_blog.repositories.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ReactionMapper {

    private final ModelMapper modelMapper;
    private final ReactionRepository reactionRepository;
    private final UserClient userClient;

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Reaction.class, ReactionResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getBlog().getId(), ReactionResponseDto::setBlogId);
                });

        TypeMap<ReactionRequestDto, Reaction> requestMap = modelMapper.createTypeMap(ReactionRequestDto.class, Reaction.class);
        requestMap.addMappings(mapper -> {
            mapper.skip(Reaction::setId);
            mapper.skip(Reaction::setType);
            mapper.skip(Reaction::setBlog);
            mapper.skip(Reaction::setReactionDate);
        });
    }

    public ReactionResponseDto entityToDto(Reaction entity) {
        UserInfoDto userId = userClient.findUserById(entity.getUserId());
        ReactionResponseDto dto = modelMapper.map(entity, ReactionResponseDto.class);
        dto.setUserId(userId);
        dto.setBlogId(entity.getBlog().getId());
        return dto;
    }

    public Reaction dtoToEntity(ReactionRequestDto dto) {
        return  modelMapper.map(dto, Reaction.class);
    }

}
