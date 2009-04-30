/**
 *    Copyright (c) 2008. Adobe Systems Incorporated.
 *    All rights reserved.
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions
 *    are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in
 *        the documentation and/or other materials provided with the
 *        distribution.
 *      * Neither the name of Adobe Systems Incorporated nor the names of
 *        its contributors may be used to endorse or promote products derived
 *        from this software without specific prior written permission.
 *
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *    PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 *    OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 package com.adobe.ac.pmd.model
{
   import com.adobe.ac.pmd.control.events.GetRulesetContentEvent;
   import com.adobe.ac.pmd.model.events.RulesetReceivedEvent;
   
   import flexunit.framework.CairngormEventSource;
   import flexunit.framework.EventfulTestCase;
   
   import mx.collections.ArrayCollection;

   public class RulesetTest extends EventfulTestCase
   {
      private var model : Ruleset;
      
      public function RulesetTest()
      {
      }

      override public function setUp():void
      {
         model = new Ruleset();
      }
      
      public function testGetRulesetContent() : void
      {
         listenForEvent( CairngormEventSource.instance, GetRulesetContentEvent.EVENT_NAME );
         
         model.getRulesetContent( "ref" );
         
         assertEvents();
         assertEquals( model, GetRulesetContentEvent( lastDispatchedExpectedEvent ).invoker );
         assertEquals( "ref", GetRulesetContentEvent( lastDispatchedExpectedEvent ).ref );
      }
      
      public function testOnReceiveRulesetContent() : void
      {
         var receivedRuleset : Ruleset = new Ruleset();
         
         listenForEvent( model, RulesetReceivedEvent.EVENT_NAME );
         
         receivedRuleset.name = "name";
         receivedRuleset.description = "description";
         receivedRuleset.rules = new ArrayCollection();
         receivedRuleset.rulesets = new ArrayCollection();
         
         model.onReceiveRulesetContent( receivedRuleset );
         
         assertEvents();
         assertEquals( model, RulesetReceivedEvent( lastDispatchedExpectedEvent ).ruleset );
         assertEquals( receivedRuleset.name, model.name );
         assertEquals( receivedRuleset.description, model.description );
         assertEquals( receivedRuleset.rules, model.rules );
         assertEquals( receivedRuleset.rulesets, model.rulesets );
      }
   }
}