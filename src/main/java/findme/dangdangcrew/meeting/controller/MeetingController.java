package findme.dangdangcrew.meeting.controller;

import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.service.MeetingService;
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

    @PostMapping("/{userId}")
    public ResponseEntity<MeetingApplicationResponseDto> createMeeting(@RequestBody MeetingRequestDto dto, @PathVariable("userId") Long userId){
        MeetingApplicationResponseDto response = meetingService.create(dto, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDetailResponseDto> getMeetingDetail(@PathVariable("meetingId") Long meetingId){
        MeetingDetailResponseDto response = meetingService.readByMeetingId(meetingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{meetingId}/{userId}")
    public ResponseEntity<MeetingApplicationResponseDto> applyMeeting(@PathVariable("meetingId") Long meetingId, @PathVariable("userId") Long userId){
        MeetingApplicationResponseDto response = meetingService.applyMeetingByMeetingId(meetingId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{metingId}/{userId}/cancel")
    public ResponseEntity<MeetingApplicationResponseDto> cancelMeetingApplication(@PathVariable("metingId") Long meetingId, @PathVariable("userId") Long userId){
        MeetingApplicationResponseDto response = meetingService.cancelMeetingApplication(meetingId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{meetingId}/status")
    public ResponseEntity<MeetingApplicationResponseDto> updateApplicationStatusByLeader(@PathVariable("meetingId") Long meetingId,
                                                                               MeetingApplicationUpdateRequestDto dto){
        MeetingApplicationResponseDto response = meetingService.changeMeetingApplicationStatusByLeader(meetingId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{meetingId}/applications")
    public ResponseEntity<List<MeetingApplicationResponseDto>> getAllMeetingApplications(@PathVariable("meetingId") Long meetingId){
        List<MeetingApplicationResponseDto> response = meetingService.readAllApplications(meetingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{meetingId}/confirmed")
    public ResponseEntity<List<MeetingApplicationResponseDto>> getConfirmedMeetingApplications(@PathVariable("meetingId") Long meetingId){
        List<MeetingApplicationResponseDto> response = meetingService.readAllConfirmed(meetingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MeetingBasicResponseDto>> getAllMeetingsByPlaceId(@RequestParam String placeId){
        List<MeetingBasicResponseDto> response = meetingService.findMeetingsByPlaceId(placeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<MeetingDetailResponseDto> updateMeeting(@PathVariable("meetingId") Long meetingId, @RequestBody MeetingRequestDto dto){
        MeetingDetailResponseDto response = meetingService.updateMeeting(meetingId, dto);
        return ResponseEntity.ok(response);
    }
}
