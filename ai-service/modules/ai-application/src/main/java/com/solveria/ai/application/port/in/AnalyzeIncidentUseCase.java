package com.solveria.ai.application.port.in;

import com.solveria.ai.application.dto.IncidentAnalysisCommandDto;
import com.solveria.ai.application.dto.IncidentAnalysisResultDto;

public interface AnalyzeIncidentUseCase {

    IncidentAnalysisResultDto analyze(IncidentAnalysisCommandDto command);
}
