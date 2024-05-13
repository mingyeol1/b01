package org.zerock.b01.repository;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    //insert
    @Test
    public void TestInsert(){//1부터 100까지 만들겠다.
        IntStream.rangeClosed(1,100).forEach(i -> {
            Board board = Board.builder()
                    .title("title..."+ i)
                    .content("content........"+ i)
                    .writer("user"+(i%10)) //유저는 0번부터 10까지. 100개가 만들어진다.
                    .build();
            Board result = boardRepository.save(board);
            log.info("BNO : "+result.getBno());
        });
    }

    @Test
    public void testSelect(){
        Long bno = 100L;

      Optional<Board> result = boardRepository.findById(bno);
      Board board = result.orElseThrow();

      log.info(board);
    }

    // update
    // Entity는 생성시 불변이면 좋이나, 변경이 일어날 경우 최소한으로 설계한다.
    @Test
    public void testUpdate(){
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        board.change("update....title1000","update content.........100");

        boardRepository.save(board);
    }

    //삭제하기
    @Test
    public void testDelete(){
        Long bno = 1L;

        boardRepository.deleteById(bno);
    }

    //Pageable과 Page<E> 타입을 이용한 페이징 처리
    // 페이징 처리는 Pageble이라는 타입의 객체를 구성해서 파라미터로 전달...
    // Pageable은 인터페이스로 설계되어 있고, 일반적으로 PageRequest.of()를 이용해서 개발한다.
    // PageRequest.of(페이지번호, 사이즈) : 페이지번호는 0번부터...
    // PageRequest.of(페이지번호, 사이즈,Sort) : Sort 객체를 통한 정렬 조건 추가
    // PageRequest.of(페이지번호, 사이즈,Sort.Direction) : 정렬 방향과 여러 속성추가 지정.

    // Pageable로 값을 넘기면 반환 타입은 Page<T>이를 이용하게 된다.
    @Test
    public void testPaging(){
        //1page order by bno desc
        Pageable pageable =
                PageRequest.of(0,10, Sort.by("bno").descending());

       Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count : "+result.getTotalElements() ); // 총 게시물 갯수
        log.info("total page : "+ result.getTotalPages()); //총 페이지 갯수
        log.info("page number : "+ result.getNumber()); //페이지번호
        log.info("page size" + result.getSize()); //페이지 사이즈
        log.info("다음 페이지 여부 : "+ result.hasNext());
        log.info("이전 페이지 여부 : "+ result.hasPrevious());

        List<Board> boardList = result.getContent();
        boardList.forEach(board -> log.info(board));
    }

    // 쿼리 메서드 및 @Query 테스트
    @Test
    public void testQueryMethod(){

        Pageable pageable = PageRequest.of(0,10);

        String keyword = "title";

       Page<Board> result = boardRepository.findByTitleContainingOrderByBnoDesc(
               keyword,
               pageable
       );
    result.getContent().forEach(board -> log.info(board));
    }

    @Test
    public void testQueryAnnotation() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("bno").descending());
        Page<Board> result = boardRepository.findKeyword("title",pageable);

        result.getContent().forEach(board -> log.info(board));
    }

    @Test
    public void testGetTime(){
        log.info(boardRepository.getTime());
    }

    @Test
    public void testSearch1(){
        //2page order by bno desc
        Pageable pageable = PageRequest.of(1,10,Sort.by("bno").descending());

        boardRepository.search1(pageable);
    }

    @Test
    public void testSearchAll(){

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(1,10,Sort.by("bno").descending());

        Page<Board> result =  boardRepository.searchAll(types,keyword,pageable);

        result.getContent().forEach(board -> log.info(board));
        log.info("사이즈 : "+ result.getSize());
        log.info("페이지 번호 : "+result.getNumber());
        log.info("이전 페이지 : " +result.hasPrevious());
        log.info("다음 페이지 : "+result.hasNext());
    }

    @Test
    public void testSearchWithReplyCount() {

        String[] types = {"t","c","w"};

        String keyword = "12345";
                                                //페이지가 0이 1부터 시작이니 0~ 10까지
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types,keyword,pageable);

        result.getContent().forEach(boardListReplyCountDTO -> {
            log.info("boardListReplyCountDTO : " + boardListReplyCountDTO);
        });

    }

}
