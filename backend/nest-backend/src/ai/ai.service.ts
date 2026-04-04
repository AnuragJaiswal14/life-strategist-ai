import { Injectable, InternalServerErrorException } from '@nestjs/common';
import OpenAI from 'openai';

@Injectable()
export class AiService {
  private openai: OpenAI;

  constructor() {
    this.openai = new OpenAI({
      apiKey: process.env.OPENAI_API_KEY || 'fake-key',
    });
  }

  async processChat(messages: { role: 'user' | 'assistant'; content: string }[]): Promise<string> {
    const prompt = `You are a friendly, conversational AI Life Strategist.
Your goal is to effortlessly collect the user's daily log data.
You need exactly THREE pieces of information to save a log:
1. Energy Level (1-10)
2. Time Spent (rough breakdown, e.g., work 5h, social 2h)
3. Habits (what good or bad habits they did today)

Rules:
- Converse normally like a human coach. Do not sound like a robot.
- Ask ONE question at a time.
- Once you deduce ALL THREE distinct data points from the chat history, you MUST output a raw JSON block and NOTHING ELSE. No wrapping text.
The JSON must strictly match:
{
  "__LOG_COMPLETE__": true,
  "energyLevel": 8,
  "timeSpent": {"work": 4, "social": 2},
  "habits": {"exercise": "done", "junk_food": "avoided"}
}
If you are missing ANY data, just reply conversationally to naturally ask for it.`;

    // Offline AI Fallback Coach (Local Dev Mode)
    if (!process.env.OPENAI_API_KEY || process.env.OPENAI_API_KEY === 'fake-key') {
      const userMessageCount = messages.filter(m => m.role === 'user').length;
      const lastMessage = messages[messages.length - 1]?.content.toLowerCase() || '';

      if (userMessageCount === 1) {
        return "I see you're checking in. I am running in Offline Local Mode! Let's log your day. On a scale of 1 to 10, how was your energy today?";
      } else if (userMessageCount === 2) {
        return "Noted. How did you allocate your time today? (e.g., worked for 4 hours, scrolled for 2)";
      } else if (userMessageCount === 3) {
        return "Got it. Finally, what habits did you stick to or break today?";
      } else {
        const energy = lastMessage.includes('tired') || lastMessage.includes('low') ? 4 : 8;
        return `{"__LOG_COMPLETE__": true, "energyLevel": ${energy}, "timeSpent": {"offline_work": 5}, "habits": {"local_testing": "done"}}`;
      }
    }

    try {
      const response = await this.openai.chat.completions.create({
        model: 'gpt-4o',
        messages: [{ role: 'system', content: prompt }, ...messages],
        temperature: 0.7,
      });
      return response.choices[0].message.content || 'Error parsing AI.';
    } catch (e) {
      console.error(e);
      throw new InternalServerErrorException('AI chat failed');
    }
  }
}
