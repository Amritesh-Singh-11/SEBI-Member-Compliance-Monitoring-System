"""
Academic ML Risk Advisory Predictor Model
-----------------------------------------
This model provides advisory predictive risk scoring for regulated stock brokers.
Uses Scikit-Learn RandomForestClassifier trained on synthetic academic RegTech features.
"""

import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier

class AcademicRiskPredictor:
    def __init__(self):
        self.model = RandomForestClassifier(n_estimators=100, random_state=42)
        self.feature_names = [
            'violationCount',
            'highSeverityViolationCount',
            'lateSubmissionCount',
            'complianceRate',
            'repeatedViolationCount',
            'previousRiskScore'
        ]
        self._train_synthetic_baseline()

    def _train_synthetic_baseline(self):
        # Generate synthetic academic training data
        np.random.seed(42)
        n_samples = 500

        viol_count = np.random.poisson(lam=1.5, size=n_samples)
        high_sev = np.random.binomial(n=viol_count, p=0.4)
        late_sub = np.random.poisson(lam=1.2, size=n_samples)
        comp_rate = np.clip(100 - (viol_count * 10 + late_sub * 8 + np.random.normal(0, 5, n_samples)), 0, 100)
        repeat_viol = np.random.binomial(n=viol_count, p=0.2)
        prev_score = np.random.uniform(10, 90, size=n_samples)

        X = pd.DataFrame({
            'violationCount': viol_count,
            'highSeverityViolationCount': high_sev,
            'lateSubmissionCount': late_sub,
            'complianceRate': comp_rate,
            'repeatedViolationCount': repeat_viol,
            'previousRiskScore': prev_score
        })

        # Generate target risk level (0=LOW, 1=MEDIUM, 2=HIGH, 3=CRITICAL)
        raw_risk = (viol_count * 15) + (high_sev * 20) + (late_sub * 12) + ((100 - comp_rate) * 0.3) + (repeat_viol * 25)
        y = np.where(raw_risk <= 30, 0, np.where(raw_risk <= 60, 1, np.where(raw_risk <= 80, 2, 3)))

        self.model.fit(X, y)

    def predict_risk(self, features_dict: dict) -> dict:
        X_input = pd.DataFrame([{
            'violationCount': features_dict.get('violationCount', 0),
            'highSeverityViolationCount': features_dict.get('highSeverityViolationCount', 0),
            'lateSubmissionCount': features_dict.get('lateSubmissionCount', 0),
            'complianceRate': features_dict.get('complianceRate', 100.0),
            'repeatedViolationCount': features_dict.get('repeatedViolationCount', 0),
            'previousRiskScore': features_dict.get('previousRiskScore', 0.0)
        }])

        probs = self.model.predict_proba(X_input)[0]
        pred_class_idx = int(np.argmax(probs))
        risk_labels = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']
        predicted_level = risk_labels[pred_class_idx]
        risk_prob = float(probs[pred_class_idx])

        # Feature Importance
        importances = self.model.feature_importances_
        feature_importance = {name: float(imp) for name, imp in zip(self.feature_names, importances)}

        return {
            "predictedRiskLevel": predicted_level,
            "riskProbability": round(risk_prob, 4),
            "classProbabilities": {risk_labels[i]: round(float(p), 4) for i, p in enumerate(probs)},
            "featureImportance": feature_importance,
            "disclaimer": "Academic ML Advisory Prediction. Deterministic rule engine remains primary regulatory determinant."
        }
