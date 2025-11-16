import streamlit as st
import pandas as pd
from main import (
    build_pipeline,
    display_output,
    MSI_CATEGORIES,
    build_pca_plot
)

st.set_page_config(page_title="College Match & ROI Tool", layout="wide")

st.title("🎓 College Match & ROI Explorer")

st.write(
    "Answer the questions below to generate a customized list of colleges "
    "ranked by similarity to your preferences, along with estimated return on investment."
)

with st.form("user_inputs"):

    st.subheader("Basic Filters")

    state_pref = st.text_input(
        "Home State (optional, 2 letters)",
        value="CA",
        max_chars=2
    ).upper()

    residency_pref = st.selectbox(
        "Residency preference:",
        ["in_state", "oos", "any"]
    )

    family_earnings = st.number_input(
        "Estimated Family Income",
        min_value=0,
        max_value=500000,
        step=1000,
        value=60000
    )

    desired_degree = st.selectbox(
        "Minimum degree you want:",
        ["non-degree", "associate", "bachelor", "master", "doctoral"]
    )


    st.subheader("Soft Preferences (Similarity Matching)")

    sector = st.selectbox("Which do you lean toward:", ["Public", "Private"])

    locality = st.selectbox("Preferred campus setting:", ["City", "Suburb", "Town", "Rural"])

    preferred_msi = st.selectbox(
        "Any MSI preference?",
        ["none"] + MSI_CATEGORIES
    )


    st.markdown("#### Numeric Preferences (You choose the target value)")

    colA, colB, colC = st.columns(3)

    with colA:
        target_enrollment = st.slider(
            "Ideal enrollment size",
            min_value=500,
            max_value=100000,
            value=30000,
            step=500
        )

    with colB:
        target_acceptance = st.slider(
            "Ideal acceptance rate (%)",
            min_value=1,
            max_value=100,
            value=20,
            step=1
        ) / 100

    with colC:
        target_ratio = st.slider(
            "Ideal student–faculty ratio",
            min_value=3,
            max_value=60,
            value=8,
            step=1
        )

    st.write("---")

    st.markdown("#### How Much Do These Preferences Matter? (Weights 1–5)")

    colW1, colW2, colW3 = st.columns(3)
    with colW1:
        w_sector = st.slider("Sector Weight", 1, 5, 3)
        w_locality = st.slider("Locality Weight", 1, 5, 4)
        w_msi = st.slider("MSI Weight", 1, 5, 3)

    with colW2:
        w_enrollment = st.slider("Enrollment Weight", 1, 5, 4)
        w_acceptance = st.slider("Acceptance Rate Weight", 1, 5, 4)
        w_ratio = st.slider("Student–Faculty Ratio Weight", 1, 5, 3)

    submitted = st.form_submit_button("Find My Colleges ✨")


if submitted:

    with st.spinner("Generating personalized match list..."):

        user_prefs = {
            "sector": sector,
            "locality": locality,
            "preferred_msi": preferred_msi if preferred_msi != "none" else None,
            "total_enrollment": target_enrollment,
            "admit_rate": target_acceptance,
            "student_faculty_ratio": target_ratio
        }

        user_weights = {
            "sector": w_sector,
            "locality": w_locality,
            "msi": w_msi,
            "total_enrollment": w_enrollment,
            "admit_rate": w_acceptance,
            "student_faculty_ratio": w_ratio
        }

        ranked_df = build_pipeline(
            state_pref,
            residency_pref,
            family_earnings,
            desired_degree,
            user_prefs,
            user_weights
        )

        results = display_output(ranked_df)


    st.success("Done! Your personalized college match results are below.")

    st.subheader("📊 Recommended Colleges")
    st.dataframe(results, use_container_width=True)

    csv_data = results.to_csv(index=False)
    st.download_button(
        label="📁 Download Full Results (CSV)",
        data=csv_data,
        file_name="college_matches.csv",
        mime="text/csv"
    )

st.subheader("📍 Visualizing Your Fit Among Colleges")

pca_fig = build_pca_plot(ranked_df, user_prefs)
st.plotly_chart(pca_fig, use_container_width=True)
