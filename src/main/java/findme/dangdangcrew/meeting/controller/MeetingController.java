package findme.dangdangcrew.meeting.controller;

import findme.dangdangcrew.global.dto.ResponseDto;
import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.service.MeetingService;
import findme.dangdangcrew.meeting.service.MeetingUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
@Tag(name = "[Meeting] Meeting API", description = "모임 관련 API")
public class MeetingController {

    private final MeetingService meetingService;
    private final MeetingUserService meetingUserService;

    @PostMapping
    @Operation(summary = "모임 생성", description = "모임을 생성합니다.")
    public ResponseEntity<ResponseDto<MeetingUserResponseDto>> createMeeting(@RequestBody MeetingRequestDto dto){
        MeetingUserResponseDto response = meetingService.create(dto);
        return ResponseEntity.ok(ResponseDto.of(response, "모임을 성공적으로 생성하였습니다."));
    }

    @GetMapping("/{meetingId}")
    @Operation(summary = "모임 상세 조회", description = "모임 정보를 조회 합니다.")
    public ResponseEntity<ResponseDto<MeetingDetailResponseDto>> getMeetingDetail(@PathVariable("meetingId") Long meetingId){
        MeetingDetailResponseDto response = meetingService.readByMeetingId(meetingId);
        return ResponseEntity.ok(ResponseDto.of(response, "모임 정보 조회를 성공하였습니다."));
    }

    @PostMapping("/{meetingId}/applications")
    @Operation(summary = "모임 참가 신청", description = "유저가 모임 참가 신청합니다.")
    public ResponseEntity<ResponseDto<MeetingUserResponseDto>> applyMeeting(@PathVariable("meetingId") Long meetingId){
        MeetingUserResponseDto response = meetingUserService.applyMeetingByMeetingId(meetingId);
        return ResponseEntity.ok(ResponseDto.of(response, "모임 참가 신청을 성공적으로 완료하였습니다."));
    }

    @PatchMapping("/{meetingId}/applications")
    @Operation(summary = "모임 참가 취소", description = "유저가 모임 참가를 취소합니다.")
    public ResponseEntity<ResponseDto<MeetingUserResponseDto>> cancelMeetingApplication(@PathVariable("meetingId") Long meetingId){
        MeetingUserResponseDto response = meetingUserService.cancelMeetingApplication(meetingId);
        return ResponseEntity.ok(ResponseDto.of(response, "모임 참가를 성공적으로 취소하였습니다."));
    }

    @GetMapping("/{meetingId}/confirmed")
    @Operation(summary = "모임 확정자 조회", description = "모임 확정자를 조회합니다.")
    public ResponseEntity<ResponseDto<List<MeetingUserResponseDto>>> getConfirmedMeetingApplications(@PathVariable("meetingId") Long meetingId){
        List<MeetingUserResponseDto> response = meetingService.readAllConfirmed(meetingId);
        return ResponseEntity.ok(ResponseDto.of(response, "모임 확정자를 성공적으로 조회하였습니다."));
    }

    @GetMapping
    @Operation(summary = "장소별 모임 조회", description = "특정 장소에 생성된 모든 모임을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MeetingBasicResponseDto>>> getAllMeetingsByPlaceId(@RequestParam("placeId") String placeId){
        List<MeetingBasicResponseDto> response = meetingService.findMeetingsByPlaceId(placeId);
        return ResponseEntity.ok(ResponseDto.of(response, "아이디가 " + placeId + "인 장소에 생성된 모든 모임 조회에 성공하였습니다."));
    }

    @GetMapping("/my")
    @Operation(summary = "내 모임 조회", description = "유저가 가입한 모든 모임을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MeetingBasicResponseDto>>> getMeetingsByUser(){
        List<MeetingBasicResponseDto> response = meetingService.findMyMeetings();
        return ResponseEntity.ok(ResponseDto.of(response, "가입한 모든 모임을 성공적으로 조회하였습니다."));
    }
}
