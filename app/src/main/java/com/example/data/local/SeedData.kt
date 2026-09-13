package com.example.data.local

import com.example.data.model.Achievement
import com.example.data.model.CommunityRoom
import com.example.data.model.Exercise
import com.example.data.model.ExerciseCategory
import com.example.data.model.ExerciseType
import com.example.data.model.VocabularyWord

object SeedData {
    val exercises = listOf(
        // 1. Speaking
        Exercise(
            id = "spk_1",
            title = "Executive Self-Introduction",
            description = "Deliver a sharp 45-second professional intro highlighting your core value and current focus.",
            category = ExerciseCategory.SPEAKING,
            type = ExerciseType.RECORDING,
            difficulty = "Intermediate",
            durationSec = 90,
            instructions = "State your name, your primary expertise, a recent high-impact win, and what you are building towards. Keep your pace between 130-150 WPM and minimize filler words ('um', 'like').",
            promptContent = "Introduce yourself to a prospective client or senior executive in under 45 seconds using the Hook-Value-Vision framework.",
            audioScript = "Hi everyone, I'm Alex. For the past five years I've led cross-functional product design initiatives...",
            sampleIdealAnswer = "Hi everyone, I'm Alex. Over the last five years, I've specialized in scaling real-time collaboration platforms that serve over a million users. Most recently, our team cut onboarding friction by 35%. Today, I'm passionate about building intuitive AI-assisted communication workflows.",
            xpReward = 40,
            tags = "career,pitch,networking"
        ),
        Exercise(
            id = "spk_2",
            title = "Handling Sudden Project Delays",
            description = "Explain a timeline slip to stakeholders without sounding defensive or uncertain.",
            category = ExerciseCategory.SPEAKING,
            type = ExerciseType.RECORDING,
            difficulty = "Advanced",
            durationSec = 120,
            instructions = "Deliver the news directly, state the primary technical bottleneck factually, outline the revised delivery milestone, and present two mitigation options.",
            promptContent = "You discovered that the third-party API integration will delay next week's beta release by 4 business days. Brief your leadership team.",
            xpReward = 45,
            tags = "leadership,crisis,workplace"
        ),

        // 2. Pronunciation
        Exercise(
            id = "pron_1",
            title = "Strategic Vocabulary Stress & Cadence",
            description = "Master multi-syllable pronunciation with accurate stress on key business terminology.",
            category = ExerciseCategory.PRONUNCIATION,
            type = ExerciseType.PRONUNCIATION_REPEAT,
            difficulty = "Intermediate",
            durationSec = 60,
            instructions = "Listen to the target phrase, pay attention to the emphasized syllables (par-TIC-u-lar-ly, stra-TE-gi-cal-ly), then repeat clearly into the microphone.",
            promptContent = "We are particularly focused on strategically scaling our cloud infrastructure.",
            audioScript = "We are particularly focused on strategically scaling our cloud infrastructure.",
            correctAnswer = "We are particularly focused on strategically scaling our cloud infrastructure.",
            explanation = "Notice the stress falling on the second syllable of 'particularly' and the third syllable of 'infrastructure'.",
            xpReward = 30,
            tags = "pronunciation,cadence,phonetics"
        ),
        Exercise(
            id = "pron_2",
            title = "Connected Speech & Flap Consonants",
            description = "Blend words naturally for effortless native-like conversational cadence.",
            category = ExerciseCategory.PRONUNCIATION,
            type = ExerciseType.PRONUNCIATION_REPEAT,
            difficulty = "Beginner",
            durationSec = 60,
            instructions = "Listen carefully to the linking sound between 'put it all' and 'matter of fact'. Repeat fluidly without robotic pauses.",
            promptContent = "As a matter of fact, we decided to put it all on the table.",
            audioScript = "As a matter of fact, we decided to put it all on the table.",
            correctAnswer = "As a matter of fact, we decided to put it all on the table.",
            explanation = "In fluid speech, 'matter of' connects with a quick flap-t sound: /'mædər əv/.",
            xpReward = 25,
            tags = "fluency,linking"
        ),

        // 3. Listening
        Exercise(
            id = "list_1",
            title = "Executive Strategy Briefing",
            description = "Listen to a director explaining Q4 priorities and extract key initiatives and emotional tone.",
            category = ExerciseCategory.LISTENING,
            type = ExerciseType.LISTENING_COMPREHENSION,
            difficulty = "Intermediate",
            durationSec = 120,
            instructions = "Play the audio passage or review the spoken briefing transcript. Answer the comprehension questions to test your nuance detection.",
            promptContent = "Listen to the Product VP describe the shift in quarterly priorities.",
            audioScript = "Good morning team. While our enterprise pipeline is strong, customer retention in mid-market accounts dropped three percent last month. Consequently, we are pausing new feature development for two sprints to double down on stability, latency reductions, and core user delight.",
            options = listOf(
                "Launch three new features immediately",
                "Pause new features for two sprints to resolve stability and retention issues",
                "Increase sales marketing budget by 30%",
                "Fire the mid-market support representatives"
            ),
            correctAnswer = "Pause new features for two sprints to resolve stability and retention issues",
            explanation = "The VP explicitly stated they are pausing new feature development for two sprints to focus on stability and latency.",
            xpReward = 35,
            tags = "listening,workplace,analysis"
        ),

        // 4. Reading
        Exercise(
            id = "read_1",
            title = "The Architecture of Constructive Feedback",
            description = "Read an executive communication memo and decipher implied intentions and structural cues.",
            category = ExerciseCategory.READING,
            type = ExerciseType.MULTIPLE_CHOICE,
            difficulty = "Intermediate",
            durationSec = 90,
            instructions = "Read the excerpt below and determine the author's primary argument regarding psychological safety.",
            promptContent = "Direct feedback without emotional empathy leads to cognitive shutdown, yet empathy without clarity produces stagnation. High-performing organizations establish 'Radical Candor'—the intersection where personal care permits direct challenge without triggering existential defensiveness.",
            options = listOf(
                "Empathy alone is the single most important factor in team happiness",
                "Direct criticism should be eliminated in modern workplaces",
                "Effective feedback requires combining genuine personal care with unambiguous direct challenge",
                "Stagnation is preferable to cognitive shutdown"
            ),
            correctAnswer = "Effective feedback requires combining genuine personal care with unambiguous direct challenge",
            explanation = "The text identifies high performance at the intersection of caring personally while directly challenging assumptions.",
            xpReward = 30,
            tags = "reading,leadership,comprehension"
        ),

        // 5. Writing
        Exercise(
            id = "wri_1",
            title = "Assertive Scope-Boundary Email",
            description = "Write a diplomatic reply to a client requesting extra features outside the contract scope.",
            category = ExerciseCategory.WRITING,
            type = ExerciseType.WRITING_RESPONSE,
            difficulty = "Intermediate",
            durationSec = 180,
            instructions = "Write a 3-sentence reply that acknowledges the request enthusiastically, clarifies that it falls outside current milestones, and proposes an add-on phase with an estimated timeline.",
            promptContent = "Client message: 'Hey team, can we also add automated invoice export and multi-currency billing into next week's release?' Write your polished response.",
            sampleIdealAnswer = "Thanks for sharing this idea—automated invoicing and multi-currency support will provide tremendous value. Because our current sprint is fully committed to delivering the core payment checkout, we can scope this out as an immediate Phase 2 enhancement starting next month. I'd be happy to share an updated roadmap and budget estimate by Thursday.",
            xpReward = 40,
            tags = "writing,business,negotiation"
        ),

        // 6. Vocabulary
        Exercise(
            id = "voc_1",
            title = "Strategic Vocabulary: 'Leverage' & 'Pragmatic'",
            description = "Select the most precise and impactful word to convey practical execution.",
            category = ExerciseCategory.VOCABULARY,
            type = ExerciseType.FILL_BLANK,
            difficulty = "Beginner",
            durationSec = 60,
            instructions = "Fill in the blank with the most appropriate professional communication term.",
            promptContent = "Rather than chasing speculative theories, the leadership team adopted a ________ approach based on verified customer telemetry.",
            options = listOf("pragmatic", "capricious", "belligerent", "superficial"),
            correctAnswer = "pragmatic",
            explanation = "'Pragmatic' means dealing with things sensibly and realistically in a way that is based on practical rather than theoretical considerations.",
            xpReward = 25,
            tags = "vocabulary,precision"
        ),

        // 7. Grammar
        Exercise(
            id = "grm_1",
            title = "Conditional Clauses in Business Negotiations",
            description = "Master subtle diplomatic conditionals to avoid premature contractual commitment.",
            category = ExerciseCategory.GRAMMAR,
            type = ExerciseType.MULTIPLE_CHOICE,
            difficulty = "Intermediate",
            durationSec = 60,
            instructions = "Choose the grammatically correct and most professional conditional phrasing.",
            promptContent = "Which sentence correctly frames a hypothetical negotiation point without committing the company prematurely?",
            options = listOf(
                "If we would agree to that discount, we went bankrupt.",
                "Were we to consider an annual commitment, we could explore tiered volume pricing.",
                "If we will agree, we give you 20% off today.",
                "Had we agree, you would get discount."
            ),
            correctAnswer = "Were we to consider an annual commitment, we could explore tiered volume pricing.",
            explanation = "Inverted conditional ('Were we to consider...') creates a sophisticated, diplomatic tone ideal for high-level negotiations.",
            xpReward = 30,
            tags = "grammar,conditionals,diplomacy"
        ),

        // 8. Scenario / Roleplay
        Exercise(
            id = "scn_1",
            title = "Executive Roleplay: The Project Scope Pushback",
            description = "Simulate a live conversation with an impatient Project Sponsor demanding an unachievable deadline.",
            category = ExerciseCategory.SCENARIO,
            type = ExerciseType.ROLEPLAY_CONVERSATION,
            difficulty = "Advanced",
            durationSec = 240,
            instructions = "Roleplay with the AI Sponsor. Maintain composure, validate their business urgency, and guide them toward a phased rollout without agreeing to impossible timelines.",
            promptContent = "Sponsor: 'Alex, we cannot move the October launch date. Our competitors are announcing next week. You just have to make it happen.' How do you reply?",
            sampleIdealAnswer = "I completely understand the market pressure from our competitors, and ensuring we capitalize on this launch window is our shared priority. To guarantee an October launch with zero downtime or security risks, let's prioritize the MVP features for the primary launch and release secondary integrations in an update two weeks later. Would you like to review the core feature cut line now?",
            xpReward = 50,
            tags = "roleplay,negotiation,management"
        ),

        // 9. Public Speaking
        Exercise(
            id = "pub_1",
            title = "The 2-Minute Innovation Pitch",
            description = "Pitch an AI-driven workflow optimization to company stakeholders in 120 seconds.",
            category = ExerciseCategory.PUBLIC_SPEAKING,
            type = ExerciseType.RECORDING,
            difficulty = "Advanced",
            durationSec = 120,
            instructions = "Record your 2-minute pitch. Focus on your opening hook, problem quantification, proposed solution, and clear call to action.",
            promptContent = "Present the case for adopting an AI Communication Training tool across the sales and support organization.",
            xpReward = 50,
            tags = "public_speaking,pitch,presentation"
        ),

        // 10. Difficult Conversations
        Exercise(
            id = "dif_1",
            title = "Addressing Chronic Meeting Tardiness",
            description = "Have a direct 1-on-1 discussion with a talented colleague who frequently arrives late to client calls.",
            category = ExerciseCategory.DIFFICULT_CONVERSATION,
            type = ExerciseType.RECORDING,
            difficulty = "Intermediate",
            durationSec = 90,
            instructions = "Use the SBI framework (Situation - Behavior - Impact). Focus on observable facts without personal attacks.",
            promptContent = "Deliver candid feedback to Jordan about arriving 10 minutes late to yesterday's client presentation.",
            sampleIdealAnswer = "Jordan, I wanted to touch base regarding yesterday's presentation with Acme Corp. When you joined ten minutes after the scheduled start, the client asked if we were having organizational issues, which created initial friction before our pitch. Your technical demos are phenomenal, and having you fully set up when the call begins reinforces client confidence. Is there anything impacting your morning schedule we can solve together?",
            xpReward = 45,
            tags = "difficult_conversation,feedback,SBI"
        ),

        // 11. Non-Verbal & Presence
        Exercise(
            id = "non_1",
            title = "Executive Presence & Deliberate Pauses",
            description = "Learn how to use strategic 2-second silence instead of filler words to project authority.",
            category = ExerciseCategory.NON_VERBAL,
            type = ExerciseType.RECORDING,
            difficulty = "Beginner",
            durationSec = 60,
            instructions = "Deliver the statement below. Whenever you see a slash [/], pause completely for two full seconds before continuing. Maintain eye contact with the camera/screen.",
            promptContent = "Our revenue surged 40% this quarter [/] because we prioritized customer retention over reckless acquisition [/] and the data proves this model works.",
            xpReward = 35,
            tags = "presence,pauses,body_language"
        )
    )

    val vocabularyList = listOf(
        VocabularyWord(
            id = "v_1",
            word = "Articulate",
            phonetic = "/ɑːrˈtɪk.jə.lət/",
            partOfSpeech = "adjective",
            meaning = "Able to express ideas clearly and effectively in speech or writing.",
            example = "She gave an articulate and persuasive presentation on team velocity.",
            synonyms = listOf("eloquent", "fluent", "lucid", "coherent"),
            category = "Communication",
            difficulty = "Intermediate",
            masteryLevel = 3
        ),
        VocabularyWord(
            id = "v_2",
            word = "Pragmatic",
            phonetic = "/præɡˈmæt.ɪk/",
            partOfSpeech = "adjective",
            meaning = "Dealing with things sensibly and realistically based on practical considerations.",
            example = "We took a pragmatic stance during the budget negotiation to prevent project cancellation.",
            synonyms = listOf("practical", "realistic", "sensible", "down-to-earth"),
            category = "Business",
            difficulty = "Intermediate",
            masteryLevel = 2
        ),
        VocabularyWord(
            id = "v_3",
            word = "Conciliatory",
            phonetic = "/kənˈsɪl.i.ə.tɔːr.i/",
            partOfSpeech = "adjective",
            meaning = "Intended to placate, pacify, or resolve animosity in a disagreement.",
            example = "Her conciliatory remarks prevented the debate from spiraling into hostility.",
            synonyms = listOf("appeasing", "pacifying", "diplomatic", "peacemaking"),
            category = "Conflict Resolution",
            difficulty = "Advanced",
            masteryLevel = 1
        ),
        VocabularyWord(
            id = "v_4",
            word = "Substantiate",
            phonetic = "/səbˈstæn.ʃi.eɪt/",
            partOfSpeech = "verb",
            meaning = "Provide evidence to support or prove the truth of a claim.",
            example = "Please substantiate your ROI forecasts with historical retention metrics.",
            synonyms = listOf("validate", "corroborate", "authenticate", "verify"),
            category = "Professional",
            difficulty = "Advanced",
            masteryLevel = 2
        ),
        VocabularyWord(
            id = "v_5",
            word = "Resilient",
            phonetic = "/rɪˈzɪl.jənt/",
            partOfSpeech = "adjective",
            meaning = "Able to withstand or recover quickly from difficult conditions.",
            example = "The engineering squad proved remarkably resilient during unexpected traffic surges.",
            synonyms = listOf("tenacious", "adaptable", "buoyant", "robust"),
            category = "Leadership",
            difficulty = "Intermediate",
            masteryLevel = 4
        ),
        VocabularyWord(
            id = "v_6",
            word = "Cognizant",
            phonetic = "/ˈkɑːɡ.nɪ.zənt/",
            partOfSpeech = "adjective",
            meaning = "Having knowledge or being fully aware of something.",
            example = "We must remain cognizant of the tight compliance deadlines.",
            synonyms = listOf("aware", "conscious", "mindful", "informed"),
            category = "Executive",
            difficulty = "Intermediate",
            masteryLevel = 1
        )
    )

    val achievements = listOf(
        Achievement(
            id = "ach_first_drill",
            title = "First Step",
            description = "Completed your first daily communication practice session.",
            icon = "Flag",
            isUnlocked = true,
            unlockedAt = "2026-09-01",
            currentProgress = 1,
            targetProgress = 1,
            xpReward = 50
        ),
        Achievement(
            id = "ach_streak_7",
            title = "Consistency Habit",
            description = "Reached a 7-day practice streak.",
            icon = "LocalFireDepartment",
            isUnlocked = true,
            unlockedAt = "2026-09-07",
            currentProgress = 7,
            targetProgress = 7,
            xpReward = 150
        ),
        Achievement(
            id = "ach_streak_30",
            title = "Unbreakable Momentum",
            description = "Maintain a 30-day streak without missing a day.",
            icon = "Whatshot",
            isUnlocked = false,
            currentProgress = 18,
            targetProgress = 30,
            xpReward = 500
        ),
        Achievement(
            id = "ach_vocab_master",
            title = "Vocabulary Virtuoso",
            description = "Master 20 active professional communication words.",
            icon = "School",
            isUnlocked = false,
            currentProgress = 12,
            targetProgress = 20,
            xpReward = 250
        ),
        Achievement(
            id = "ach_filler_slayer",
            title = "Filler Word Slayer",
            description = "Deliver 5 speaking recordings with under 1 filler word per minute.",
            icon = "Mic",
            isUnlocked = true,
            unlockedAt = "2026-09-11",
            currentProgress = 5,
            targetProgress = 5,
            xpReward = 200
        ),
        Achievement(
            id = "ach_scenario_master",
            title = "Diplomatic Negotiator",
            description = "Successfully complete 5 complex roleplay scenarios.",
            icon = "Psychology",
            isUnlocked = false,
            currentProgress = 3,
            targetProgress = 5,
            xpReward = 300
        )
    )

    val communityRooms = listOf(
        CommunityRoom(
            id = "room_1",
            title = "Executive & Startup Pitch Practice",
            topic = "Pitching your idea in under 90 seconds with instant peer feedback",
            description = "A supportive room where founders and team leads take turns delivering 90-second pitches and receiving constructive feedback.",
            participantsCount = 14,
            hostName = "Sarah L. (Coach)",
            isLive = true,
            tags = listOf("Pitch", "Startup", "Confidence"),
            roomType = "Stage Room"
        ),
        CommunityRoom(
            id = "room_2",
            title = "Toastmasters Impromptu Table Topics",
            topic = "Random communication prompt, 1 minute to answer with no prep",
            description = "Practice quick thinking, structured delivery, and eliminating filler words when caught off guard.",
            participantsCount = 28,
            hostName = "Marcus K.",
            isLive = true,
            tags = listOf("Impromptu", "Fluency", "Fun"),
            roomType = "Open Circle"
        ),
        CommunityRoom(
            id = "room_3",
            title = "Job Interview & Behavioral Simulation",
            topic = "Answering 'Tell me about a time you failed' using STAR method",
            description = "Live mock interviews with actionable scoring on composure, clarity, and conciseness.",
            participantsCount = 19,
            hostName = "Elena R. (HR Lead)",
            isLive = true,
            tags = listOf("Career", "Interviews", "STAR"),
            roomType = "Workshop"
        ),
        CommunityRoom(
            id = "room_4",
            title = "Casual Conversational English Lounge",
            topic = "Small talk, humor, pop culture, and travel storytelling",
            description = "Low-pressure open discussion to build natural casual fluency and spontaneous speech.",
            participantsCount = 35,
            hostName = "David B.",
            isLive = true,
            tags = listOf("Casual", "Social", "Storytelling"),
            roomType = "Casual Lounge"
        )
    )
}
