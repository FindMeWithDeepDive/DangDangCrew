package findme.dangdangcrew.meeting.controller;

import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.service.MeetingService;
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

    @PostMapping
    @Operation(summary = "모임 생성", description = "모임을 생성합니다.")
    public ResponseEntity<MeetingUserResponseDto> createMeeting(@RequestBody MeetingRequestDto dto){
        MeetingUserResponseDto response = meetingService.create(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{meetingId}")
    @Operation(summary = "모임 상세 조회", description = "모임 정보를 조회 합니다.")
    public ResponseEntity<MeetingDetailResponseDto> getMeetingDetail(@PathVariable("meetingId") Long meetingId){
        MeetingDetailResponseDto response = meetingService.readByMeetingId(meetingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{meetingId}/applications/apply")
    @Operation(summary = "모임 참가 신청", description = "유저가 모임 참가 신청합니다.")
    public ResponseEntity<MeetingUserResponseDto> applyMeeting(@PathVariable("meetingId") Long meetingId){
        MeetingUserResponseDto response = meetingService.applyMeetingByMeetingId(meetingId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{meetingId}/applications/cancel")
    @Operation(summary = "모임 참가 취소", description = "유저가 모임 참가를 취소합니다.")
    public ResponseEntity<MeetingUserResponseDto> cancelMeetingApplication(@PathVariable("meetingId") Long meetingId){
        MeetingUserResponseDto response = meetingService.cancelMeetingApplication(meetingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{meetingId}/applications/status")
    @Operation(summary = "(모임 생성자) 모임 상태 변경", description = "모임 생성자가 모임 신청 유저들을 확정/취소합니다.")
    public ResponseEntity<MeetingUserResponseDto> updateApplicationStatusByLeader(@PathVariable("meetingId") Long meetingId,
                                                                               @RequestBody MeetingUserStatusUpdateRequestDto dto){
        MeetingUserResponseDto response = meetingService.changeMeetingApplicationStatusByLeader(meetingId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{meetingId}/applications")
    @Operation(summary = "(모임 생성자) 모임 신청자 조회", description = "모임 생성자가 모임 참가를 신청한 모든 유저 목록을 조회합니다.")
    public ResponseEntity<List<MeetingUserResponseDto>> getAllMeetingApplications(@PathVariable("meetingId") Long meetingId){
        List<MeetingUserResponseDto> response = meetingService.readAllApplications(meetingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{meetingId}/confirmed")
    @Operation(summary = "모임 확정자 조회", description = "모임 확정자를 조회합니다.")
    public ResponseEntity<List<MeetingUserResponseDto>> getConfirmedMeetingApplications(@PathVariable("meetingId") Long meetingId){
        List<MeetingUserResponseDto> response = meetingService.readAllConfirmed(meetingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "장소별 모임 조회", description = "특정 장소에 생성된 모든 모임을 조회합니다.")
    public ResponseEntity<List<MeetingBasicResponseDto>> getAllMeetingsByPlaceId(@RequestParam String placeId){
        List<MeetingBasicResponseDto> response = meetingService.findMeetingsByPlaceId(placeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @Operation(summary = "내 모임 조회", description = "유저가 가입한 모든 모임을 조회합니다.")
    public ResponseEntity<List<MeetingBasicResponseDto>> getMeetingsByUser(){
        List<MeetingBasicResponseDto> response = meetingService.findMyMeetings();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{meetingId}")
    @Operation(summary = "(모임 생성자) 모임 수정", description = "모임 생성자가 모임을 수정합니다.")
    public ResponseEntity<MeetingDetailResponseDto> updateMeeting(@PathVariable("meetingId") Long meetingId, @RequestBody MeetingRequestDto dto){
        MeetingDetailResponseDto response = meetingService.updateMeeting(meetingId, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{meetingId}/cancel")
    @Operation(summary = "(모임 생성자) 모임 삭제", description = "모임 생성자가 모임을 삭제합니다.")
    public ResponseEntity<Void> deleteMeeting(@PathVariable("meetingId") Long meetingId){
        meetingService.deleteMeeting(meetingId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{meetingId}/quit")
    @Operation(summary = "(모임 생성자) 모임 종료", description = "모임 생성자가 모임 종료시 종료 버튼을 누릅니다.")
    public ResponseEntity<List<MeetingUserResponseDto>> quitMeeting(@PathVariable("meetingId") Long meetingId){
        List<MeetingUserResponseDto> response = meetingService.quitMeeting(meetingId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{meetingId}/check")
    @Operation(summary = "(모임 생성자) 모임 참석 여부 변경", description = "모임 생성자가 모임 종료시 모임 참석 여부를 확인합니다.")
    public ResponseEntity<List<MeetingUserResponseDto>> checkUserMeetingStatus(@PathVariable("meetingId") Long meetingId, @RequestBody List<MeetingUserStatusUpdateRequestDto> dto){
        List<MeetingUserResponseDto> response = meetingService.checkAttendedOrAbsent(meetingId, dto);
        return ResponseEntity.ok(response);
    }
}
