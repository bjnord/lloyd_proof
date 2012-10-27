# Author::     Brent J. Nordquist <mailto:brent@nordist.net>
# Copyright::  Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# License::    {CC Attribution-ShareAlike 3.0 Unported}[http://creativecommons.org/licenses/by-sa/3.0/]

class CorrectionsController < ApplicationController
  respond_to :html, :except => :upload

  def index
    respond_with(@corrections = Correction.all)
  end

  # Accept and save an array of corrections, returning a corresponding
  # array with the status of each save. Each correction to be saved should
  # have a +sync_id+ attribute; this attribute is not saved, and only
  # needs to be unique for each correction in this call. The return status
  # array will have each status tagged with the matching +sync_id+.
  #
  # == JSON POST format
  #
  #   {"corrections":[{"sync_id":17,"current_text":"To Kil A Mockingbird"},{"sync_id":21,"current_text":""}]}
  #
  # == JSON return format
  #
  #   {"upload_status":[{"sync_id":17,"status":"ok"},{"sync_id":21,"status":"error","errors":["Current text cannot be blank"]}]}
  #
  def upload
    @statuses = params[:corrections].collect do |c|
      Correction.create_and_return_status(c)
    end
    respond_to do |format|
      format.json { render :json => { :upload_status => @statuses } }
    end
  end
end
