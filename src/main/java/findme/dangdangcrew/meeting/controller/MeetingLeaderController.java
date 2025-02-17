package findme.dangdangcrew.meeting.controller;

import findme.dangdangcrew.global.dto.ResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingDetailResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.dto.MeetingUserResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingUserStatusUpdateRequestDto;
import findme.dangdangcrew.meeting.service.MeetingLeaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings/leader")
@RequiredArgsConstructor
@Tag(name = "[Meeting Leader] Meeting Leader API", description = "모임 리더 관련 API")
public class MeetingLeaderController {

    private final MeetingLeaderService meetingLeaderService;

    @PutMapping("/{meetingId}/applications")
    @Operation(summary = "모임 상태 변경", description = "모임 생성자가 모임 신청 유저들을 확정/취소합니다.")
    public ResponseEntity<ResponseDto<MeetingUserResponseDto>> updateApplicationStatusByLeader(@PathVariable("meetingId") Long meetingId,
                                                                                               @RequestBody MeetingUserStatusUpdateRequestDto dto){
        MeetingUserResponseDto response = meetingLeaderService.changeMeetingApplicationStatusByLeader(meetingId, dto);
        return ResponseEntity.ok(ResponseDto.of(response, "모임 신청을 한 유저들의 상태를 성공적으로 변경하였습니다."));
    }

    @GetMapping("/{meetingId}/applications")
    @Operation(summary = "모임 신청자 조회", description = "모임 생성자가 모임 참가를 신청한 모든 유저 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<MeetingUserResponseDto>>> getAllMeetingApplications(@PathVariable("meetingId") Long meetingId){
        List<MeetingUserResponseDto> response = meetingLeaderService.readAllApplications(meetingId);
        return ResponseEntity.ok(ResponseDto.of(response, "모임 참가자 목록을 성공적으로 조회하였습니다."));
    }

    @PutMapping("/{meetingId}")
    @Operation(summary = "모임 수정", description = "모임 생성자가 모임을 수정합니다.")
    public ResponseEntity<ResponseDto<MeetingDetailResponseDto>> updateMeeting(@PathVariable("meetingId") Long meetingId, @RequestBody MeetingRequestDto dto){
        MeetingDetailResponseDto response = meetingLeaderService.updateMeeting(meetingId, dto);
        return ResponseEntity.ok(ResponseDto.of(response, "모임 수정을 성공적으로 완료하였습니다."));
    }

    @PatchMapping("/{meetingId}/cancel")
    @Operation(summary = "모임 삭제", description = "모임 생성자가 모임을 삭제합니다.")
    public ResponseEntity<ResponseDto> deleteMeeting(@PathVariable("meetingId") Long meetingId){
        meetingLeaderService.deleteMeeting(meetingId);
        return ResponseEntity.ok(ResponseDto.of(null, "모임 삭제를 성공적으로 완료하였습니다."));
    }

    @PatchMapping("/{meetingId}/quit")
    @Operation(summary = "모임 종료", description = "모임 생성자가 모임 종료시 종료 버튼을 누릅니다.")
    public ResponseEntity<ResponseDto<List<MeetingUserResponseDto>>> quitMeeting(@PathVariable("meetingId") Long meetingId){
        List<MeetingUserResponseDto> response = meetingLeaderService.quitMeeting(meetingId);
        return ResponseEntity.ok(ResponseDto.of(response, "모임 종료를 성공적으로 완료하였습니다."));
    }

    @PatchMapping("/{meetingId}/attendance")
    @Operation(summary = "모임 참석 여부 변경", description = "모임 생성자가 모임 종료시 모임 참석 여부를 확인합니다.")
    public ResponseEntity<ResponseDto<List<MeetingUserResponseDto>>> checkUserMeetingStatus(@PathVariable("meetingId") Long meetingId, @RequestBody List<MeetingUserStatusUpdateRequestDto> dto){
        List<MeetingUserResponseDto> response = meetingLeaderService.checkAttendedOrAbsent(meetingId, dto);
        return ResponseEntity.ok(ResponseDto.of(response, dto.size() + "개의 유저들의 상태를 성공적으로 변경하였습니다."));
    }
}
