from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, Dict
from app.model import AcademicRiskPredictor

app = FastAPI(
    title="SEBI Academic RegTech ML Advisory Risk Service",
    description="Microservice providing advisory machine learning risk probability predictions for regulated stock brokers.",
    version="1.0.0-academic"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

predictor = AcademicRiskPredictor()

class RiskPredictionRequest(BaseModel):
    memberId: Optional[int] = None
    violationCount: int = 0
    highSeverityViolationCount: int = 0
    lateSubmissionCount: int = 0
    complianceRate: float = 100.0
    repeatedViolationCount: int = 0
    previousRiskScore: float = 0.0
    memberType: str = "STOCK_BROKER"

@app.get("/")
def health_check():
    return {
        "service": "SEBI RegTech Academic ML Risk Advisory",
        "status": "HEALTHY",
        "version": "1.0.0"
    }

@app.post("/predict-risk")
def predict_risk(request: RiskPredictionRequest):
    try:
        res = predictor.predict_risk(request.dict())
        return res
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
